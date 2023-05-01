package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
        	BufferedReader br = new BufferedReader(new InputStreamReader(in));
        	
        	String line = br.readLine();
        	if(line == null) { return; }
        	
        	String firstLine = line;
        	byte[] body = null;
        	
        	
        	// HTTP 요청 정보 전체 출력 start---------------------------------------------------------------------
        	System.out.println("HTTP 요청 정보 전체 출력 start---------------------------------------------------------------------");
        	while(!"".equals(line)) {
        		System.out.println(line);
        		line = br.readLine();
        	}
        	System.out.println("HTTP 요청 정보 전체 출력 end-----------------------------------------------------------------------");
        	// HTTP 요청 정보 전체 출력 end-----------------------------------------------------------------------
        	
        	
        	// index.html로 이동 start------------------------------------------------------------------------
        	// if문 1번째 방법 start--------------------------------------------------------
        	// '?'로 조건을 걸었을 경우, 쿼리스트링이 없을 때에 화면 전환이 자유롭다.
        	if(!firstLine.contains("?")) {
	        	String[] tokens = firstLine.split(" ");
	        	String url = tokens[1];
	        		
	        	body = Files.readAllBytes(new File("./webapp" + url).toPath());
        	}
        	// if문 1번째 방법 end----------------------------------------------------------
        	// if문 2번째 방법 start--------------------------------------------------------
        	// 문제 발생.......
        	// 특정 html로 조건을 걸었을 경우, 모든 html에 대해 조건을 걸어 주어야 한다.
//        	String[] tokens = firstLine.split(" ");
//        	String url = tokens[1];
//        	
//        	if(url.equals("/index.html")) {
//        		body = Files.readAllBytes(new File("./webapp" + url).toPath());
//        	}
        	// if문 2번째 방법 end----------------------------------------------------------
        	// index.html로 이동 end--------------------------------------------------------------------------
        	
        	
        	// GET방식 회원가입 start----------------------------
        	if(firstLine.contains("?")) {
        		String[] tokens = firstLine.split(" ");
	        	String url = tokens[1];
	        	
	        	String requestPath = url.split("[?]")[0];
	        	String params = url.split("[?]")[1];
	        	
	        	System.out.println("requestPath : " + requestPath);
	        	System.out.println("params      : " + params);
        	}
        	// GET방식 회원가입 end------------------------------
        	
        	
        	//
//        	while(!"".equals(line)) {
//        		String[] tokens = line.split(" ");
//        		for(String token : tokens) { System.out.println(token); }
//        		line = br.readLine();
//        	}
        	
        	
        		
        	// -------------------------------------------------
        		
            DataOutputStream dos = new DataOutputStream(out);
//            byte[] body = "Hello World".getBytes();	// 구조상 재정의.
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
