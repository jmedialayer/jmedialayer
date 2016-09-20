package jmedialayer.backends.psvita;

import com.jtransc.annotation.*;
import com.jtransc.io.JTranscIoTools;
import com.jtransc.io.JTranscSyncIO;
import com.jtransc.target.Cpp;
import com.jtransc.time.JTranscClock;
import jmedialayer.backends.Backend;
import jmedialayer.graphics.Bitmap32;
import jmedialayer.graphics.G1;
import jmedialayer.input.Input;
import jmedialayer.input.Keys;
import jmedialayer.backends.ResourcePromise;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@SuppressWarnings("JavacQuirks")
@JTranscAddHeader(target = "cpp", value = {
	"extern \"C\" {",
	"#include <psp2/io/dirent.h>",
	"#include <psp2/ctrl.h>",
	"#include <psp2/touch.h>",
	"#include <psp2/display.h>",
	"#include <psp2/gxm.h>",
	"#include <psp2/kernel/sysmem.h>",
	"#include <psp2/types.h>",
	"#include <psp2/moduleinfo.h>",
	"#include <psp2/kernel/processmgr.h>",
	//"#include <vita2d.h>",
	"}",
	"#define SCREEN_W 960",
	"#define SCREEN_H 544",
	"#define align_mem(addr, align) (((addr) + ((align) - 1)) & ~((align) - 1))",
	"static SceDisplayFrameBuf fb[2];",
	"static int cur_fb = 0;",
	"static SceCtrlData pad = {0};",
	"static SceTouchData touch = {0};",
})
@JTranscAddLibraries(target = "cpp", value = {
	//"jpeg",
	//"png",
	//"vita2d",
	//"z",
	"c",
	"SceKernel_stub",
	"SceKernel_stub",
	"SceDisplay_stub",
	"SceGxm_stub",
	"SceSysmodule_stub",
	"SceCtrl_stub",
	"ScePgf_stub",
	"SceTouch_stub",
	"SceCommonDialog_stub",
	//"z",
	//"png",
	//"jpeg",
	//"vita2d",
})
@JTranscAddMembers(target = "cpp", value = {
})
public class HenkakuPsvitaBackend extends Backend {
	public HenkakuPsvitaBackend() {
		init();
	}

	static public final int WIDTH = 960;
	static public final int HEIGHT = 544;

	static private void init() {
		JTranscClock.impl = new JTranscClock.Impl(JTranscClock.impl) {
			@Override
			@JTranscMethodBody(target = "cpp", value = {
				"::sceKernelDelayThread((int32_t)(p0 * 1000.0));"
			})
			native public void sleep(double ms);
		};

		JTranscSyncIO.impl = new JTranscSyncIO.Impl(JTranscSyncIO.impl) {
			@Override
			@JTranscMethodBody(target = "cpp", value = {
				"char name[1024] = {0};",
				"auto str = N::istr3(p0);",
				"sprintf(name, \"%s\", str.c_str());",
				"std::vector<std::string> out;",
				"SceIoDirent dir;",
				"SceUID d = sceIoDopen(name);",
				"if (d >= 0) {",
				"	while (sceIoDread(d, &dir) > 0) {",
				"		out.push_back(std::string(dir.d_name));",
				"	}",
				"	sceIoDclose(d);",
				"}",
				"return N::strArray(out);"
			})
			native public String[] list(String file);

			@Override
			public JTranscSyncIO.ImplStream open(String path, int mode) throws FileNotFoundException {
				return super.open(path, mode);
			}

			@Override
			public String normalizePath(String path) {
				while (path.startsWith("/")) path = path.substring(1);
				if (!path.contains(":")) path = "app0:/" + path;
				return path;
			}

			@Override
			public boolean isAbsolute(String path) {
				return path.contains(":");
			}

			//@Override
			//@JTranscMethodBody(target = "cpp", value = {
			//	"struct stat stat_buf;",
			//	"char name[1024] = {0};",
			//	"::strcpy(name, N::istr3(p0).c_str());",
			//	"int rc = ::stat(name, &stat_buf);",
			//	"return (rc == 0) ? stat_buf.st_size : -1;",
			//})
			//public long getLength(String path) {
			//	return 0L;
			//}
		};
		//JTranscSyncIO.impl.setCwd("app0:/");
		JTranscSyncIO.impl.setCwd("/");

		init_video();
	}

	@Override
	public int getNativeWidth() {
		return WIDTH;
	}

	@Override
	public int getNativeHeight() {
		return HEIGHT;
	}

	static private void init_video() {
		Cpp.v_raw("int ret;");

		Cpp.v_raw("SceGxmInitializeParams params;");

		Cpp.v_raw("params.flags                        = 0x0;");
		Cpp.v_raw("params.displayQueueMaxPendingCount  = 0x2;"); //Double buffering
		Cpp.v_raw("params.displayQueueCallback         = 0x0;");
		Cpp.v_raw("params.displayQueueCallbackDataSize = 0x0;");
		Cpp.v_raw("params.parameterBufferSize          = (16 * 1024 * 1024);");

		// Initialize the GXM
		Cpp.v_raw("ret = sceGxmInitialize(&params);");

		// Setup framebuffers
		Cpp.v_raw("fb[0].size        = sizeof(fb[0]);");
		Cpp.v_raw("fb[0].pitch       = SCREEN_W;");
		Cpp.v_raw("fb[0].pixelformat = SCE_DISPLAY_PIXELFORMAT_A8B8G8R8;");
		Cpp.v_raw("fb[0].width       = SCREEN_W;");
		Cpp.v_raw("fb[0].height      = SCREEN_H;");

		Cpp.v_raw("fb[1].size        = sizeof(fb[1]);");
		Cpp.v_raw("fb[1].pitch       = SCREEN_W;");
		Cpp.v_raw("fb[1].pixelformat = SCE_DISPLAY_PIXELFORMAT_A8B8G8R8;");
		Cpp.v_raw("fb[1].width       = SCREEN_W;");
		Cpp.v_raw("fb[1].height      = SCREEN_H;");


		// Allocate memory for the framebuffers
		Cpp.v_raw("fb[0].base = (void *)(size_t){% SMETHOD jmedialayer.backends.psvita.HenkakuPsvitaBackend:alloc_gpu_mem %}(SCE_KERNEL_MEMBLOCK_TYPE_USER_CDRAM_RW, (SCREEN_W * SCREEN_H * 4));");
		Cpp.v_raw("fb[1].base = (void *)(size_t){% SMETHOD jmedialayer.backends.psvita.HenkakuPsvitaBackend:alloc_gpu_mem %}(SCE_KERNEL_MEMBLOCK_TYPE_USER_CDRAM_RW, (SCREEN_W * SCREEN_H * 4));");

		// Display the framebuffer 0
		Cpp.v_raw("cur_fb = 0;");
		swap_buffers();
	}

	@Override
	protected G1 createG1() {
		return new G1() {
			@Override
			public void updateBitmap(Bitmap32 bmp) {
				updated = true;

				//Cpp.v_raw("memset(fb[cur_fb].base, 0xFF, SCREEN_W*SCREEN_H*4);");
				if (bmp.width == WIDTH && bmp.height == HEIGHT) {
					writeInts(getCurrentBufferStart(), bmp.data, 0, bmp.area);
				} else {
					int minwidth = Math.min(bmp.width, WIDTH);
					int minheight = Math.min(bmp.height, HEIGHT);
					for (int y = 0; y < minheight; y++) {
						writeInts(getCurrentBufferStart() + (y * (WIDTH * 4)), bmp.data, bmp.index(0, y), minwidth);
					}
				}
			}
		};
	}

	@Override
	protected Input createInput() {
		return new Input() {
			private final int PSP2_CTRL_SELECT = 1 << 0;
			private final int PSP2_CTRL_START = 1 << 3;
			private final int PSP2_CTRL_UP = 1 << 4;
			private final int PSP2_CTRL_RIGHT = 1 << 5;
			private final int PSP2_CTRL_DOWN = 1 << 6;
			private final int PSP2_CTRL_LEFT = 1 << 7;
			private final int PSP2_CTRL_LTRIGGER = 1 << 8;
			private final int PSP2_CTRL_RTRIGGER = 1 << 9;
			private final int PSP2_CTRL_TRIANGLE = 1 << 12;
			private final int PSP2_CTRL_CIRCLE = 1 << 13;
			private final int PSP2_CTRL_CROSS = 1 << 14;
			private final int PSP2_CTRL_SQUARE = 1 << 15;
			private final int PSP2_CTRL_ANY = 1 << 16;

			@Override
			public boolean isPressing(Keys key) {
				int buttons = getButtons();
				switch (key) {
					case UP:
						return (buttons & PSP2_CTRL_UP) != 0;
					case DOWN:
						return (buttons & PSP2_CTRL_DOWN) != 0;
					case LEFT:
						return (buttons & PSP2_CTRL_LEFT) != 0;
					case RIGHT:
						return (buttons & PSP2_CTRL_RIGHT) != 0;
					case START:
						return (buttons & PSP2_CTRL_START) != 0;
				}
				return false;
			}
		};
	}

	@Override
	protected void preStep() {
		super.preStep();
		input_read();
	}

	private boolean updated = false;

	@Override
	protected void postStep() {
		super.postStep();
		if (updated) {
			updated = false;
			swap_buffers();
		}
	}

	@Override
	protected void waitNextFrame() {
		Cpp.v_raw("::sceDisplayWaitVblankStart();");
	}

	@Override
	protected void preEnd() {
		Cpp.v_raw("::sceGxmUnmapMemory(fb[0].base);");
		Cpp.v_raw("::sceGxmUnmapMemory(fb[1].base);");
		Cpp.v_raw("::sceGxmTerminate();");
	}

	static private void swap_buffers() {
		Cpp.v_raw("sceDisplaySetFrameBuf(&fb[cur_fb], SCE_DISPLAY_SETBUF_NEXTFRAME);");
		Cpp.v_raw("cur_fb ^= 1;");
	}

	@JTranscKeep // Required @JTranscKeep since Cpp.v_raw is not used for references just yet
	static private int alloc_gpu_mem(int type, int size) {
		Cpp.v_raw("int type = p0;");
		Cpp.v_raw("int size = p1;");
		Cpp.v_raw("void *mem = NULL;");
		Cpp.v_raw("int ret;");

		if (type == Cpp.i_raw("SCE_KERNEL_MEMBLOCK_TYPE_USER_CDRAM_RW")) {
			Cpp.v_raw("size = align_mem(size, 256 * 1024)");
		} else {
			Cpp.v_raw("size = align_mem(size, 4 * 1024)");
		}

		Cpp.v_raw("int uid = sceKernelAllocMemBlock(\"gxm\", type, size, NULL);");
		Cpp.v_raw("ret = sceKernelGetMemBlockBase(uid, &mem);");
		Cpp.v_raw("ret = sceGxmMapMemory(mem, size, SCE_GXM_MEMORY_ATTRIB_RW);");
		return Cpp.i_raw("(int32_t)mem");
	}

	static private void input_read() {
		Cpp.v_raw("sceCtrlPeekBufferPositive(0, &pad, 1);");
		Cpp.v_raw("sceTouchPeek(0, &touch, 1);");
	}

	static private int getButtons() {
		return Cpp.i_raw("pad.buttons");
	}

	static private int getCurrentBufferStart() {
		return Cpp.i_raw("(int32_t)(int32_t *)(fb[cur_fb].base)");
	}

	static private void writeInts(int ptr, int[] data, int offset, int count) {
		Cpp.v_raw("::memcpy((void *)p0, (void *)(GET_OBJECT(JA_I, p1)->getOffsetPtr(p2)), sizeof(int32_t) * p3);");
	}

	@Override
	protected File getFileFromPath(String path) {
		if (path.contains(":")) {
			return new File(path);
		} else {
			return new File("app0:/" + path);
		}
	}

	@Override
	public ResourcePromise<Bitmap32> loadBitmap32(String path) {
		return _loadBitmap32Sync(path);
	}
}

