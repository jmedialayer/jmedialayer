package jmedialayer.graphics;

import com.jtransc.util.JTranscBase64;
import jmedialayer.util.FastMemByte;
import jmedialayer.util.FastMemInt;

public class EmbeddedFont {
	static private byte[] msx_font = JTranscBase64.decode("" +
		"AAAAAAAAAAA8QqWBpZlCPDx+2///22Y8bP7+/nw4EAAQOHz+fDgQABA4VP5UEDgA" +
		"EDh8/v4QOAAAAAAwMAAAAP///+fn////OESCgoJEOADHu319fbvH/w8DBXmIiIhw" +
		"OERERDgQfBAwKCQkKCDgwDwkPCQk5NwYEFQ47jhUEAAQEBB8EBAQEBAQEP8AAAAA" +
		"AAAA/xAQEBAQEBDwEBAQEBAQEB8QEBAQEBAQ/xAQEBAQEBAQEBAQEAAAAP8AAAAA" +
		"AAAAHxAQEBAAAADwEBAQEBAQEB8AAAAAEBAQ8AAAAACBQiQYGCRCgQECBAgQIECA" +
		"gEAgEAgEAgEAEBD/EBAAAAAAAAAAAAAAICAgIAAAIABQUFAAAAAAAFBQ+FD4UFAA" +
		"IHigcCjwIADAyBAgQJgYAECgQKiQmGAAECBAAAAAAAAQIEBAQCAQAEAgEBAQIEAA" +
		"IKhwIHCoIAAAICD4ICAAAAAAAAAAICBAAAAAeAAAAAAAAAAAAGBgAAAACBAgQIAA" +
		"cIiYqMiIcAAgYKAgICD4AHCICBBggPgAcIgIMAiIcAAQMFCQ+BAQAPiA4BAIEOAA" +
		"MECA8IiIcAD4iBAgICAgAHCIiHCIiHAAcIiIeAgQYAAAACAAACAAAAAAIAAAICBA" +
		"GDBgwGAwGAAAAPgA+AAAAMBgMBgwYMAAcIgIECAAIABwiAhoqKhwACBQiIj4iIgA" +
		"8EhIcEhI8AAwSICAgEgwAOBQSEhIUOAA+ICA8ICA+AD4gIDwgICAAHCIgLiIiHAA" +
		"iIiI+IiIiABwICAgICBwADgQEBCQkGAAiJCgwKCQiACAgICAgID4AIjYqKiIiIgA" +
		"iMjIqJiYiABwiIiIiIhwAPCIiPCAgIAAcIiIiKiQaADwiIjwoJCIAHCIgHAIiHAA" +
		"+CAgICAgIACIiIiIiIhwAIiIiIhQUCAAiIiIqKjYiACIiFAgUIiIAIiIiHAgICAA" +
		"+AgQIECA+ABwQEBAQEBwAAAAgEAgEAgAcBAQEBAQcAAgUIgAAAAAAAAAAAAAAPgA" +
		"QCAQAAAAAAAAAHAIeIh4AICAsMiIyLAAAABwiICIcAAICGiYiJhoAAAAcIj4gHAA" +
		"ECgg+CAgIAAAAGiYmGgIcICA8IiIiIgAIABgICAgcAAQADAQEBCQYEBASFBgUEgA" +
		"YCAgICAgcAAAANCoqKioAAAAsMiIiIgAAABwiIiIcAAAALDIyLCAgAAAaJiYaAgI" +
		"AACwyICAgAAAAHiA8AjwAEBA8EBASDAAAACQkJCQaAAAAIiIiFAgAAAAiKioqFAA" +
		"AACIUCBQiAAAAIiImGgIcAAA+BAgQPgAGCAgQCAgGAAgICAAICAgAMAgIBAgIMAA" +
		"QKgQAAAAAAAAACBQ+AAAAHCIgICIcCBgkAAAkJCQaAAQIHCI+IBwACBQcAh4iHgA" +
		"SABwCHiIeAAgEHAIeIh4ACAAcAh4iHgAAHCAgIBwEGAgUHCI+IBwAFAAcIj4gHAA" +
		"IBBwiPiAcABQAABgICBwACBQAGAgIHAAQCAAYCAgcABQACBQiPiIACAAIFCI+IgA" +
		"ECD4gPCA+AAAAGwSfpBuAD5QkJzwkJ4AYJAAYJCQYACQAABgkJBgAEAgAGCQkGAA" +
		"QKAAoKCgUABAIACgoKBQAJAAkJCwUBDgUABwiIiIcABQAIiIiIhwACAgeICAeCAg" +
		"GCQg+CDiXACIUCD4IPggAMCgoMiciIiMGCAg+CAgIEAQIHAIeIh4ABAgAGAgIHAA" +
		"IEAAYJCQYAAgQACQkJBoAFCgAKDQkJAAKFAAyKiYiAAAcAh4iHgA+ABgkJCQYADw" +
		"IAAgQICIcAAAAAD4gIAAAAAAAPgICAAAhIiQqFSECByEiJCoWKg8CCAAACAgICAA" +
		"AAAkSJBIJAAAAJBIJEiQAChQIFCI+IgAKFBwCHiIeAAoUABwICBwAChQACAgIHAA" +
		"KFAAcIiIcABQoABgkJBgAChQAIiIiHAAUKAAoKCgUAD8SEhI6AhQIABQAFBQUBAg" +
		"wETIVOxUngQQqEAAAAAAAAAgUIhQIAAAiBAgQIAoAAB8qKhoKCgoADhAMEhIMAhw" +
		"AAAAAAAA///w8PDwDw8PDwAA//////////8AAAAAAAAAAAA8PAAAAP///////wAA" +
		"wMDAwMDAwMAPDw8P8PDw8Pz8/Pz8/Pz8AwMDAwMDAwM/Pz8/Pz8/PxEiRIgRIkSI" +
		"iEQiEYhEIhH+fDgQAAAAAAAAAAAQOHz+gMDg8ODAgAABAwcPBwMBAP9+PBgYPH7/" +
		"gcPn///nw4Hw8PDwAAAAAAAAAAAPDw8PDw8PDwAAAAAAAAAA8PDw8DMzzMwzM8zM" +
		"ACAgUFCI+AAgIHAgcCAgAAAAAFCIqFAA//////////8AAAAA//////Dw8PDw8PDw" +
		"Dw8PDw8PDw//////AAAAAAAAaJCQkGgAMEhIcEhIcMD4iICAgICAAPhQUFBQUJgA" +
		"+IhAIECI+AAAAHiQkJBgAABQUFBQaICAAFCgICAgIAD4IHCoqHAg+CBQiPiIUCAA" +
		"cIiIiFBQ2AAwQEAgUFBQIAAAAFCoqFAACHCoqKhwgAA4QID4gEA4AHCIiIiIiIgA" +
		"APgA+AD4AAAgIPggIAD4AMAwCDDAAPgAGGCAYBgA+AAQKCAgICAgICAgICAgIKBA" +
		"ACAA+AAgAAAAUKAAUKAAAAAYJCQYAAAAADB4eDAAAAAAAAAAMAAAAD4gICCgYCAA" +
		"oFBQUAAAAABAoCBA4AAAAAA4ODg4ODgAAAAAAAAAAA=="
	);

	static private Bitmap32[] glyphs;

	static private void initOnce() {
		if (glyphs != null) return;
		glyphs = new Bitmap32[0x100];
		FastMemByte.selectSRC(msx_font);
		int p = 0;
		for (int n = 0; n < 0xFF; n++) {
			Bitmap32 bmp = glyphs[n] = new Bitmap32(8, 8);
			FastMemInt.selectDST(bmp.data);
			int nn = 0;
			for (int y = 0; y < 8; y++) {
				int b = FastMemByte.getSRC(p++);
				for (int x = 0; x < 8; x++) {
					int cc = (((b >> (7 - x)) & 1) != 0) ? 0xFFFFFFFF : 0x00000000;
					FastMemInt.setDST(nn++, cc);
				}
			}
		}
	}

	static public void draw(Bitmap32 out, int startx, int starty, String text) {
		initOnce();
		int x = startx;
		int y = starty;
		for (int n = 0; n < text.length(); n++) {
			int c = text.charAt(n) & 0xFF;
			if (c == (int)'\n') {
				x = startx;
				y += 8;
			} else {
				Bitmap32 glyph = glyphs[c];
				if (glyph != null) out.draw(glyph, x, y);
				x += 8;
			}
		}

	}
}
