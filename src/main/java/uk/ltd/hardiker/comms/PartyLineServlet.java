package uk.ltd.hardiker.comms;

import com.thoughtworks.xstream.XStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PartyLineServlet extends HttpServlet {

    private static int TIMEOUT = 60 * 1000;

    XStream xStream;
    Queue<MessageBlock> messages = new ConcurrentLinkedQueue<MessageBlock>();

    public PartyLineServlet() {
        xStream = new XStream();
        xStream.setClassLoader( getClass().getClassLoader() );
        xStream.alias("message", MessageBlock.class);

        {
            MessageBlock msg = new MessageBlock();
            msg.setTime( System.currentTimeMillis() );
            msg.setKey("a");
            messages.add( msg );
        }

        {
            MessageBlock msg = new MessageBlock();
            msg.setTime( System.currentTimeMillis() );
            msg.setKey("b");
            messages.add( msg );
        }

        {
            MessageBlock msg = new MessageBlock();
            msg.setTime( System.currentTimeMillis() );
            msg.setKey("c");
            messages.add( msg );
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long sinceTime = new Long(req.getQueryString());
        List<MessageBlock> myMessages = new ArrayList<MessageBlock>();

        Long killBefore = System.currentTimeMillis() - TIMEOUT;
        List<MessageBlock> killMessages = new ArrayList<MessageBlock>();

        for (MessageBlock block : messages) {
            if (block.getTime() > sinceTime) myMessages.add( block );
            if (block.getTime() < killBefore) killMessages.add( block );
        }

        messages.removeAll( killMessages );

        xStream.toXML(myMessages, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String data = req.getQueryString();
        data = data.replaceAll("&lt;", "<");
        data = data.replaceAll("&gt;", ">");
        data = data.replaceAll("%3C", "<");
        data = data.replaceAll("%3E", ">");

        MessageBlock block = (MessageBlock) xStream.fromXML( data );
        block.setTime( System.currentTimeMillis() );

        messages.add( block );
    }

}
