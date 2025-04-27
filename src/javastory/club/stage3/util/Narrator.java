package javastory.club.stage3.util;

public class Narrator {
	//
	private String playerClass;
	private TalkingAt position;
	private boolean silentMode;
	private boolean formatted;

	public Narrator(Object player, TalkingAt position) {
		//
		this.playerClass = player.getClass().getSimpleName();
		this.position = position;
		this.formatted = false;
		this.silentMode = false;
	}

	public void setFormatted(boolean formatted) {
		this.formatted = formatted;
	}

	public void keepSilent() {
		//
		this.silentMode = true;
	}

	public void keepTalkative() {
		//
		this.silentMode = false;
	}

	public void say (String message) {
		//
		printOut(appendTabs().append(formatMessage(message)));
	}

	public void sayln (String message) {
		//
		printOut(appendTabs().append(formatMessage(message)).append("\n"));
	}

	public void sayNewLine() {
		//
		System.out.print("\n");
	}

	private void printOut(StringBuffer message) {
		//
		if (!silentMode) {
			System.out.print(message.toString());
		} else {
			System.out.print("*");
		}
	}

	private String formatMessage(String message) {
		//
		if (formatted) {
			return String.format(" <:%s> %s", playerClass, message);
		} else {
			return message;
		}
	}

	private StringBuffer appendTabs () {
		//
		StringBuffer buffer = new StringBuffer();

		for (int i=0; i<this.position.tabCount(); i++) {
			buffer.append("\t");
		}

		return buffer;
	}

	public void sleep(int seconds) {
		//
		try {
			Thread.sleep(seconds*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
