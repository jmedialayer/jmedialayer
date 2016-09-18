package jmedialayer.backends;

import com.jtransc.annotation.JTranscMethodBody;
import com.jtransc.annotation.JTranscMethodBodyList;

public class BackendSelector {
	@JTranscMethodBodyList({
		@JTranscMethodBody(target = "js", value = "return {% CONSTRUCTOR jmedialayer.backends.browser.BrowserBackend:()V %}();"),
		// @TODO: Check this! (crashes on psvita)
		//@JTranscMethodBody(target = "cpp", value = "return {% CONSTRUCTOR jmedialayer.backends.psvita.HenkakuPsvitaBackend:()V %}();")
		@JTranscMethodBody(target = "cpp", value = "auto out = SOBJ(new {% CLASS jmedialayer.backends.psvita.HenkakuPsvitaBackend %}); GET_OBJECT({% CLASS jmedialayer.backends.psvita.HenkakuPsvitaBackend %}, out)->{% METHOD jmedialayer.backends.psvita.HenkakuPsvitaBackend:<init>:()V %}(); return out;"),
	})
	static public Backend getDefault() {
		try {
			return (Backend)Class.forName("jmedialayer.backends.awt.AwtBackend").newInstance();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
