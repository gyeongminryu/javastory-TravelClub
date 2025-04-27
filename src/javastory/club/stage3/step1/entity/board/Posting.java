package javastory.club.stage3.step1.entity.board;

import java.util.ArrayList;
import java.util.List;

import javastory.club.stage3.step1.entity.Entity;
import javastory.club.stage3.step1.entity.club.CommunityMember;
import javastory.club.stage3.util.DateUtil;

public class Posting implements Entity {
  //
	private String usid;			// format - BoardId:00021
	private String title;
	private String writerEmail;		// member email
	private String contents;
	private String writtenDate;
	private int readCount;

	private String boardId;

	public Posting() {
		//
		this.readCount = 0;
	}

	public Posting(SocialBoard board, String title, String writerEmail, String contents) {
		//
		this();
		this.usid = board.nextPostingId(); //board의 nextPostingId()
		this.boardId = board.getId();
		this.title = title;
		this.writerEmail = writerEmail;
		this.contents = contents;
		this.writtenDate = DateUtil.today();
	}

	public Posting(String postingId, String boardId, String title, String writerEmail, String contents) {
		//
		this.usid = postingId;
		this.boardId = boardId;
		this.title = title;
		this.writerEmail = writerEmail;
		this.contents = contents;
		this.writtenDate = DateUtil.today();
	}

	@Override
	public String toString() {
		//
		StringBuilder builder = new StringBuilder();

		builder.append("Posting id: " + usid);
    	builder.append(", title: " + title);
		builder.append(", writer email: " + writerEmail);
		builder.append(", read count: " + readCount);
		builder.append(", written date: " + writtenDate);
		builder.append(", contents: " + contents);
    	builder.append(", board id: " + boardId);

 	    return builder.toString();
	}

	public static List<Posting> getSamples(SocialBoard board) {
		//
		List<Posting> postings = new ArrayList<Posting>();

		CommunityMember leader = CommunityMember.getSample();
		Posting leaderPosting = new Posting(board, "The club intro", leader.getEmail(), "Hello, it's good to see you.");
		postings.add(leaderPosting);

		String postingUsid = board.nextPostingId();//왜 여기서 또 nextPostingId() 해줌? 어차피 Posting 객체화해줄 때 될텐데?

		CommunityMember member = CommunityMember.getSample();
		Posting memberPosting = new Posting(board, "self intro", member.getEmail(), "Hello, My name is minsoo.");
		memberPosting.setUsid(postingUsid);
		postings.add(memberPosting);

		return postings;
	}

	@Override
	public String getId() {
		return usid;
	}

	public String getUsid() {
		return usid;
	}

	public void setUsid(String usid) {
		this.usid = usid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getWriterEmail() {
		return writerEmail;
	}

	public void setWriterEmail(String writerEmail) {
		this.writerEmail = writerEmail;
	}

	public int getReadCount() {
		return readCount;
	}

	public void setReadCount(int readCount) {
		this.readCount = readCount;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getWrittenDate() {
		return writtenDate;
	}

	public void setWrittenDate(String writtenDate) {
		this.writtenDate = writtenDate;
	}

	public String getBoardId() {
		return boardId;
	}

	public void setBoardId(String boardId) {
		this.boardId = boardId;
	}
}
