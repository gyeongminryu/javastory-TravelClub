package javastory.club.stage3.step2.ui.console;

import javastory.club.stage3.util.ConsoleUtil;
import javastory.club.stage3.util.Narrator;
import javastory.club.stage3.util.TalkingAt;

public class ClubMembershipConsole {
	//
	private ConsoleUtil consoleUtil;
	private Narrator narrator;

	public ClubMembershipConsole() {
		//
		this.narrator = new Narrator(this, TalkingAt.Left);
		this.consoleUtil = new ConsoleUtil(narrator);
	}

	public void findClub() {
		//
		consoleUtil.getValueOf("\n You've select target club find menu [Enter to go back].");
	}

	public void add() {
		//
		consoleUtil.getValueOf("\n You've select the club's membership register menu [Enter to go back].");
	}

	public void find() {
		//
		consoleUtil.getValueOf("\n You've select the club's membership find menu [Enter to go back].");
	}

	public void modify() {
		//
		consoleUtil.getValueOf("\n You've select the club's membership modify menu [Enter to go back].");
	}

	public void remove() {
		//
		consoleUtil.getValueOf("\n You've select the club's membership remove menu [Enter to go back].");
	}

}