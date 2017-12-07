/*
Hector Ferronato 2017
- Learning HTTP protocols
- Compare socket solution to HttpsURLConnection (java library)

History:
    -Tried HTTP/1.0 but missed host (port 80)
    -Tried HTTP/1.0 with host on header
    -Tried HTTP/1.1 with host
    -Tried HTTP/1.1 with SSL (port 443)

Try: https://www.google.com/
     https://www.amazon.com/
     https://www.youtube.com/
     https://www.ebay.com/
     https://www.microsoft.com/

Details for further exploration:
    -Redirects
    -TimeOut (wait for hanging requests)
    -Images and styles (reading html from string loses encoding)
    -Filter http response to separate header and body (html)
 */


package com.example.hector.openwebpage;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import javax.net.SocketFactory;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button openPageButton = (Button) findViewById(R.id.openPageButton);

        openPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Running", "Button Clicked");

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                TextView editText = (TextView) findViewById(R.id.editText);
                String pageAddr = editText.getText().toString();

                if (!pageAddr.contains("https")) {
                    Context context = getApplicationContext();
                    CharSequence text = "Please type an URL with 'https' protocol!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else {
                    try {
                        final SocketFactory socketFactory = SSLSocketFactory.getDefault();
                        URL u = new URL(pageAddr);
                        String host = u.getHost();
                        final Socket socket = socketFactory.createSocket(host, 443);
                        socket.setSoTimeout(5000);
                        final String request = "GET / HTTP/1.1\r\nConnection: close\r\nHost:" + host + "\r\n\r\n";

                        final OutputStream outputStream = socket.getOutputStream();
                        outputStream.write(request.getBytes());

                        InputStream inputStream = socket.getInputStream();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) > 0) {
                            byteArrayOutputStream.write(buffer, 0, bytesRead);
                        }
                        WebView webview = (WebView) findViewById(R.id.webview);
                        webview.getSettings().setJavaScriptEnabled(true);
                        webview.loadData(byteArrayOutputStream.toString(), "text/html", "UTF-8");

                        URL url = new URL(pageAddr);
                        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        inputStream = connection.getInputStream();
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        buffer = new byte[1024];
                        while ((bytesRead = inputStream.read(buffer)) > 0) {
                            byteArrayOutputStream.write(buffer, 0, bytesRead);
                        }
                        WebView webview2 = (WebView) findViewById(R.id.webView2);
                        webview2.getSettings().setJavaScriptEnabled(true);
                        webview2.loadData(byteArrayOutputStream.toString(), "text/html", "UTF-8");


                    } catch (MalformedURLException ex) {
                    } catch (IOException ex) {
                    }
                }
            }
        });
    } //oncreate
} //main
