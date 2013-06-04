package com.ioabsoftware.gameraven.views;

import java.util.HashMap;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.Shape;
import android.text.Html;
import android.text.Spanned;
import android.util.StateSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ioabsoftware.gameraven.AllInOneV2;
import com.ioabsoftware.gameraven.R;
import com.ioabsoftware.gameraven.URLImageParser;
import com.ioabsoftware.gameraven.networking.Session;
import com.ioabsoftware.gameraven.networking.HandlesNetworkResult.NetDesc;

public class MessageView extends LinearLayout implements View.OnClickListener {

	private String userContent, messageID, boardID, topicID;
	private Element messageContent;
	private AllInOneV2 aio;
	
	public String getUser() {
		return userContent;
	}
	
	public String getMessageID() {
		return messageID;
	}
	
	public String getTopicID() {
		return topicID;
	}
	
	public String getBoardID() {
		return boardID;
	}
	
	public String getMessageDetailLink() {
		return Session.ROOT + "/boards/" + boardID + "/" + topicID + "/" + messageID;
	}
	
	public String getUserDetailLink() {
		return Session.ROOT + "/users/" + userContent.replace(' ', '+') + "/boards";
	}
	
	
	
	public MessageView(final AllInOneV2 aioIn, String userIn, String userTitles, String postNum,
					   String postTimeIn, Element messageIn, String BID, String TID, String MID, int hlColor) {
		super(aioIn);
		
		aioIn.wtl("starting mv creation");
		
		aio = aioIn;
        
        userContent = userIn;
        messageContent = messageIn;
        messageID = MID;
        topicID = TID;
        boardID = BID;

		aio.wtl("stored vals");
		
		LayoutInflater inflater = (LayoutInflater) aio.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.msgview, this);

		aio.wtl("inflated layout");
        
        ((TextView) findViewById(R.id.mvUser)).setText(userContent + userTitles);
        ((TextView) findViewById(R.id.mvUser)).setTextColor(AllInOneV2.getAccentTextColor());
        ((TextView) findViewById(R.id.mvPostNumber)).setText("#" + postNum + ", " + postTimeIn);
        ((TextView) findViewById(R.id.mvPostNumber)).setTextColor(AllInOneV2.getAccentTextColor());

		aio.wtl("set text and color for user and post number");
        
        String html = null;
        if (messageContent.getElementsByClass("board_poll").isEmpty()) {
    		aio.wtl("no poll");
        	html = messageContent.html();
		}
        else {
    		aio.wtl("there is a poll");
        	Element ed = messageContent.clone();
        	ed.getElementsByClass("board_poll").first().remove();
        	html = ed.html();

    		LinearLayout pollWrapper = (LinearLayout) findViewById(R.id.mvPollWrapper);
    		LinearLayout innerPollWrapper = new LinearLayout(aio);
    		
    		ShapeDrawable s = new ShapeDrawable();
    		Paint p = s.getPaint();
    		p.setStyle(Paint.Style.STROKE);
    		p.setStrokeWidth(10);
    		p.setColor(Color.parseColor(ColorPickerPreference.convertToARGB(AllInOneV2.getAccentColor())));
    		
    		pollWrapper.setBackgroundDrawable(s);
			pollWrapper.addView(new HeaderView(aio, messageContent.getElementsByClass("poll_head").first().text()));
			pollWrapper.addView(innerPollWrapper);

    		innerPollWrapper.setPadding(15, 0, 15, 15);
    		innerPollWrapper.setOrientation(VERTICAL);
        	if (messageContent.getElementsByTag("form").isEmpty()) {
        		// poll_foot_left
        		TextView t;
        		for (Element e : messageContent.getElementsByClass("table_row")) {
        			Elements c = e.children();
        			t = new TextView(aio);
        			t.setText(c.get(0).text() + ": " + c.get(1).text() + ", " + c.get(3).text() + " votes");
        			innerPollWrapper.addView(t);
        		}
        		
        		String foot = messageContent.getElementsByClass("poll_foot_left").text();
        		if (foot.length() > 0) {
        			t = new TextView(aio);
        			t.setText(foot);
        			innerPollWrapper.addView(t);
        		}
        		
        	}
        	else {
            	final String action = "/boards/" + boardID + "/" + topicID;
    			String key = messageContent.getElementsByAttributeValue("name", "key").attr("value");
    			
    			int x = 0;
    			for (Element e : messageContent.getElementsByAttributeValue("name", "poll_vote")) {
    				x++;
    				Button b = new Button(aio);
    				b.setText(e.nextElementSibling().text());
    				final HashMap<String, String> data = new HashMap<String, String>();
    				data.put("key", key);
    				data.put("poll_vote", Integer.toString(x));
    				data.put("submit", "Vote");
    				b.setOnClickListener(new OnClickListener() {
    					@Override
    					public void onClick(View v) {
    						aio.getSession().post(NetDesc.TOPIC, action, data);
    					}
    				});
    				innerPollWrapper.addView(b);
    			}
    			
    			Button b = new Button(aio);
    			b.setText("View Results");
    			b.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						aio.getSession().get(NetDesc.TOPIC, action + "?results=1", null);
					}
				});
    			innerPollWrapper.addView(b);
    			
    			innerPollWrapper.setVisibility(View.VISIBLE);
        	}
        }

		aio.wtl("html var set");
        
        TextView message = (TextView) findViewById(R.id.mvMessage);
//        URLImageParser p = new URLImageParser(message, aio);
//        Spanned htmlSpan = Html.fromHtml(html, p, null);
//        message.setText(htmlSpan);
        message.setText(Html.fromHtml(html, null, null));
        message.setLinkTextColor(AllInOneV2.getAccentColor());

		aio.wtl("set message text, linkified, set color");
        
        findViewById(R.id.mvTopWrapper).setOnClickListener(this);
        if (hlColor == 0)
        	findViewById(R.id.mvTopWrapper).setBackgroundDrawable(AllInOneV2.getMsgHeadSelector().getConstantState().newDrawable());
        else {
        	float[] hsv = new float[3];
    		Color.colorToHSV(hlColor, hsv);
        	if (AllInOneV2.getSettingsPref().getBoolean("useWhiteAccentText", false)) {
    			// color is probably dark
    			if (hsv[2] > 0)
    				hsv[2] *= 1.2f;
    			else
    				hsv[2] = 0.2f;
    		}
    		else {
    			// color is probably bright
    			hsv[2] *= 0.8f;
    		}
    		
    		int msgSelectorColor = Color.HSVToColor(hsv);
    		
    		StateListDrawable hlSelector = new StateListDrawable();
    		hlSelector.addState(new int[] {android.R.attr.state_focused}, new ColorDrawable(msgSelectorColor));
    		hlSelector.addState(new int[] {android.R.attr.state_pressed}, new ColorDrawable(msgSelectorColor));
    		hlSelector.addState(StateSet.WILD_CARD, new ColorDrawable(hlColor));
    		
    		findViewById(R.id.mvTopWrapper).setBackgroundDrawable(hlSelector.getConstantState().newDrawable());
        }

		aio.wtl("set click listener and drawable for top wrapper");
        
        if (AllInOneV2.isAccentLight())
        	((ImageView) findViewById(R.id.mvMessageMenuIcon)).setImageResource(R.drawable.ic_info_light);
        

		aio.wtl("finishing mv creation");
	}

	@Override
	public void onClick(View v) {
		aio.messageMenuClicked(MessageView.this);
	}
	
	public String getMessageForQuoting() {
		return processContent(true);
	}
	
	public String getMessageForEditing() {
		return processContent(false);
	}

	private String processContent(boolean removeSig) {
		Element clonedBody = messageContent.getElementsByClass("msg_body").first().clone();
		if (!clonedBody.getElementsByClass("board_poll").isEmpty()) {
			clonedBody.getElementsByClass("board_poll").first().remove();
		}

		String finalBody = clonedBody.html();

		if (removeSig) {
			int sigStart = finalBody.lastIndexOf("---");
			if (sigStart != -1)
				finalBody = finalBody.substring(0, sigStart);
		}
		
		finalBody = finalBody.replace("<span class=\"fspoiler\">", "<spoiler>").replace("</span>", "</spoiler>");
		
		while (finalBody.contains("<a href")) {
			int start = finalBody.indexOf("<a href");
			int end = finalBody.indexOf(">", start) + 1;
			finalBody = finalBody.replace(finalBody.substring(start, end), "");
		}
		finalBody = finalBody.replace("</a>", "");
		if (finalBody.endsWith("<br />"))
			finalBody = finalBody.substring(0, finalBody.length() - 6);
		finalBody = finalBody.replace("\n", "");
		finalBody = finalBody.replace("<br />", "\n");
		
		finalBody = StringEscapeUtils.unescapeHtml4(finalBody);
		return finalBody;
	}
}