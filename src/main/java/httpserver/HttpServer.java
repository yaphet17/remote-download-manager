package httpserver;

import downloadmanager.DownloadManagerController;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.util.Headers;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.concurrent.Callable;

import static io.undertow.Handlers.path;

class getWebhook implements HttpHandler{

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE,"application/json");
        var value=exchange.getQueryParameters().get("name");
        exchange.getResponseSender().send("{\"message\":\"webhook\"}");


    }
}
class setUrl implements HttpHandler{
    private final DownloadManagerController controller;
    public setUrl(DownloadManagerController controller) {
        this.controller=controller;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE,"application/json");
        String url=exchange.getQueryParameters().get("url").getFirst();
        Platform.runLater(()->{
            controller.startDownload(url);
        });

        exchange.getResponseSender().send(String.format("{\"message\":\"%s\"}","download started"));
    }
}
public class HttpServer extends Task<Void> {
    private final DownloadManagerController controller;

    public HttpServer(DownloadManagerController controller) {
        this.controller = controller;
    }

    @Override
    protected Void call() throws Exception {
        System.out.println("called");
        PathHandler pathHandler=new PathHandler();
        pathHandler.addPrefixPath("/setUrl",new setUrl(controller));
        Undertow server=Undertow.builder()
                .addHttpListener(8080,"192.168.43.167")
                .setHandler(pathHandler).build();
        System.out.println("server started listening on port 8080");
        server.start();
        return null;
    }
}
