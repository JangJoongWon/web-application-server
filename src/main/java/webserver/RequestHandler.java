package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;
import util.HttpRequestUtils;
import util.IOUtils;

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
        	DataOutputStream dos = new DataOutputStream(out);
        	
        	BufferedReader br = new BufferedReader(new InputStreamReader(in));
        	
        	String line = br.readLine();
        	if(line == null) { return; }	// 무한루프에 대비한 예외처리.
        	
        	String[] tokens = line.split(" ");
        	
        	String method = tokens[0];	// "GET or POST"
        	String url = tokens[1];
        	System.out.println("method :: " + method);
        	System.out.println("url    :: " + url);
        	
        	byte[] body = null;
        	
        	int contentLength = 0;	// "POST방식 회원가입"에 사용
        	
        	
        	System.out.println("-------------------------------------HTTP 요청 정보 전체 출력 start-------------------------------------");
        	while(!"".equals(line)) {
        		// "POST방식 회원가입"에 사용 start----------------------------------------
        		tokens = line.split(" ");
//        		System.out.println(tokens[0]);
        		
        		if(tokens[0].equals("Content-Length:")) {
        			contentLength = Integer.parseInt(tokens[1]);
        		}
        		// "POST방식 회원가입"에 사용 end------------------------------------------
        		
        		System.out.println(line);	// 실제로 출력을 한다.
        		line = br.readLine();		// 수신 받은 데이터를 읽으며 커서를 옮기는 역할을 한다.
        	}
        	System.out.println("-------------------------------------HTTP 요청 정보 전체 출력 end---------------------------------------");
        	
        	
//        	System.out.println("--------------------------------------index.html로 이동 start--------------------------------------");
        	// if문 1번째 방법 start--------------------------------------------------------
//        	if(url.equals("/index.html")) {
//        		body = Files.readAllBytes(new File("./webapp" + url).toPath());
//        	}
        	// 문제 발생......
        	// 특정 html로 조건을 걸었을 경우, 모든 html에 대해 조건을 걸어 주어야 한다.
        	// if문 1번째 방법 end----------------------------------------------------------
        	
        	// if문 2번째 방법 start--------------------------------------------------------
        	// '?'로 조건을 걸었을 경우, 쿼리스트링이 없을 때에 화면 전환이 자유롭다.
//	        if(!url.contains("?")) {
//	        	body = Files.readAllBytes(new File("./webapp" + url).toPath());
//        	}
	        // 문제 발생......
	        // POST방식으로 쿼리스트링이 들어올 경우, GET방식과 달리 '?'가 'url'에 존재하지 않아 if문이 작동하게 된다.
        	// if문이 작동하면 없는 페이지를 찾아가게 되면서 오류가 발생한다.
        	// if문 2번째 방법 end----------------------------------------------------------
        	
        	// if문 3번째 방법 start--------------------------------------------------------
	        if(!url.contains("?") && (method != null && !method.equals("POST"))) {
	        	body = Files.readAllBytes(new File("./webapp" + url).toPath());
	        	response200Header(dos, body.length);	// "POST방식 회원가입"에서 response302Header를 사용하면서 옮김.
        	}
        	// if문 3번째 방법 end----------------------------------------------------------
//        	System.out.println("---------------------------------------index.html로 이동 end---------------------------------------");
        	
        	
//        	System.out.println("---------------------------------------GET방식 회원가입 start---------------------------------------");
//        	if(url.contains("?")) {
//	        	String requestPath = url.split("[?]")[0];
//	        	String params = url.split("[?]")[1];						// url중 쿼리스트링.
//	        	String decodeParams = URLDecoder.decode(params, "UTF-8");	// url중 params를 디코딩.
//	        	System.out.println("requestPath  :: " + requestPath);
//	        	System.out.println("params       :: " + params);
//	        	System.out.println("decodeParams :: " + decodeParams);
//	        	
//	        	Map<String, String> pqs = HttpRequestUtils.parseQueryString(decodeParams);
//	        	
//	        	String userId = pqs.get("userId");
//	        	String password = pqs.get("password");
//	        	String name = pqs.get("name");
//	        	String email = pqs.get("email");
//	        	
//	        	User user = new User(userId, password, name, email);
//	        	System.out.println(user.toString());
//        	}
//        	System.out.println("----------------------------------------GET방식 회원가입 end----------------------------------------");
        	
        	
//        	System.out.println("---------------------------------------POST방식 회원가입 start---------------------------------------");
        	if(method != null && method.equals("POST")) {
        		// body 내용 읽기 1번 방법 start---------------------------------
//        		String queryString = br.readLine();
        		// 문제 발생......
        		// body 내용 읽기 1번 방법 end-----------------------------------
				
        		// body 내용 읽기 2번 방법 start---------------------------------
        		// readData는 read()를 사용한다. read()와 readLine()의 차이를 알 것.
        		String queryString = IOUtils.readData(br, contentLength);
				// body 내용 읽기 2번 방법 end-----------------------------------
//        		System.out.println("queryString       :: " + queryString);
				
				String decodeQueryString = URLDecoder.decode(queryString, "UTF-8");
//				System.out.println("decodeQueryString :: " + decodeQueryString);
				
				Map<String, String> pqs = HttpRequestUtils.parseQueryString(decodeQueryString);
				
				String userId = pqs.get("userId");
	        	String password = pqs.get("password");
	        	String name = pqs.get("name");
	        	String email = pqs.get("email");
	        	
	        	User user = new User(userId, password, name, email);
//	        	System.out.println(user.toString());
	        	
	        	response302Header(dos, "/index.html");
        	}
//        	System.out.println("----------------------------------------POST방식 회원가입 end----------------------------------------");
        	
        	
        	System.out.println();
        	
//            DataOutputStream dos = new DataOutputStream(out);
//            byte[] body = "Hello World".getBytes();	// 구조상 재정의.
//            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    // "POST방식 회원가입"에서 사용	// 공부 필요
    private void response302Header(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + url + "\r\n");
            dos.writeBytes("\r\n");
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
