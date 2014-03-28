/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package systech.device.check;

/**
 * @author systech.io
 */

import java.net.Socket;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;


public class SystechDeviceCheck {

    public static void main(String[] args) throws Exception {
        int hg;
        int sa;

        /*
         for (String s: args) {
         System.out.println(s);
         }
        
         */
        String ip = args[0];
        int port = Integer.parseInt(args[1]);
        String user = args[2];
        String pwd = args[3];
        String ua = args[4];

        if (ua.equals("spa2102") || ua.equals("spa3102") || ua.equals("Aastra") || ua.equals("Adtran")) {
            sa = standardauth(ip, port, user, pwd);

            if (sa == 200) {
                System.out.println("FAIL Default user/pass  for " + ip + ":" + port + "::::" + ua);
            } else if (sa == 10000) {
                System.out.println("NO CONNECT for " + ip + ":" + port + "::::" + ua);

            } else {
                System.out.println("TEST OK for " + ip + ":" + port + "::::" + ua);
            }

        } else if (ua.equals("spa122")) {
            hg = httpGet(ip, port, user, pwd);

            if (hg == 10000) {
                System.out.println("NO CONNECT for " + ip + ":" + port + "::::" + ua);

            } else if (hg % 2 == 0) {
                System.out.println("TEST OK for " + ip + ":" + port + "::::" + ua);
            } else {
                System.out.println("Default user/pass  for " + ip + ":" + port + "::::" + ua);
            }

        }

    } // end main

    //STANDARD AUTH
    public static int standardauth(String ip, int port, String user, String pwd) throws Exception {

        int status = 0;

        String url = "http://" + ip + ":" + port + "/";

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(ip, port),
                new UsernamePasswordCredentials(user, pwd));
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        try {
            HttpGet httpget = new HttpGet(url);

            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                ;
                status = response.getStatusLine().getStatusCode();
                EntityUtils.consume(response.getEntity());

            } catch (Exception e) {
                status = 10000;
            } finally {
                response.close();
            }
        } catch (Exception e) {

            status = 10000;
        } finally {
            httpclient.close();
        }

        return status;

    }
    //get method

    public static int httpGet(String ip, int port, String user, String pwd) throws Exception {

        String content = "";
        String search = "Admin user is not allowed to login";
        int status = 0;

        HttpProcessor httpproc = HttpProcessorBuilder.create()
                .add(new RequestContent())
                .add(new RequestTargetHost())
                .add(new RequestConnControl())
                .add(new RequestUserAgent("Windows IE 6"))
                .add(new RequestExpectContinue(true)).build();

        HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

        HttpCoreContext coreContext = HttpCoreContext.create();
        HttpHost host = new HttpHost(ip, port);
        coreContext.setTargetHost(host);

        DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(8 * 1024);
        ConnectionReuseStrategy connStrategy = DefaultConnectionReuseStrategy.INSTANCE;

        try {

            // default hashes for cisco spa112 and 
            String[] targets = {
                "/login.cgi?enc=0&submit_button=login&user=admin&pwd=498836900e3cb4d343b96f3f1c578f4a",
                "/login.cgi?enc=0&submit_button=login&user=cisco&pwd=0fa58742e186c8e5ce52ba133f8714cb"};

            for (int i = 0; i < targets.length; i++) {

                if (!conn.isOpen()) {
                    Socket socket = new Socket(host.getHostName(), host.getPort());
                    conn.bind(socket);
                }

                BasicHttpRequest request = new BasicHttpRequest("GET", targets[i]);

                httpexecutor.preProcess(request, httpproc, coreContext);
                HttpResponse response = httpexecutor.execute(request, conn, coreContext);
                httpexecutor.postProcess(response, httpproc, coreContext);

                content = EntityUtils.toString(response.getEntity());

                if (content.toLowerCase().contains(search.toLowerCase()) == true) {
                    status++;
                }

                if (!connStrategy.keepAlive(response, coreContext)) {
                    conn.close();
                } else {

                }
            }

        } catch (Exception e) {
            content = "Unable to connect Possibly a NAT issue or not gonfigured on proper port";
            status = 10000;
        } finally {
            conn.close();
        }

        return status;

    }
}
