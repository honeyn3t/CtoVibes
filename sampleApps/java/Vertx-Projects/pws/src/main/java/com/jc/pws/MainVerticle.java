package com.jc.pws;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.handlebars.HandlebarsTemplateEngine;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;


public class MainVerticle extends AbstractVerticle {
	private final static Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);
	
	public void start(Promise<Void> startPromise) throws Exception {
	    LOGGER.info("This is an INFO TEST MESSAGE");
	  	LOGGER.debug("This is a DEBUG TEST MESSAGE");
	  	LOGGER.warn("This is a WARN TEST MESSAGE");
	  	LOGGER.error("This is an ERROR TEST MESSAGE");
	  	VertxOptions options = new VertxOptions(); 
	  	options.setMaxEventLoopExecuteTime(Long.MAX_VALUE);
	  	vertx = Vertx.vertx(options);
	  	 HandlebarsTemplateEngine engine = HandlebarsTemplateEngine.create(vertx);
		  Router router = Router.router(vertx);
		  router.route().handler(StaticHandler.create());
		  Set<String> allowedHeaders = new HashSet<>();
		    allowedHeaders.add("x-requested-with");
		    allowedHeaders.add("Access-Control-Allow-Origin");
		    allowedHeaders.add("origin");
		    allowedHeaders.add("Content-Type");
		    allowedHeaders.add("accept");
		    allowedHeaders.add("X-PINGARUNER");

		    Set<HttpMethod> allowedMethods = new HashSet<>();
		    allowedMethods.add(HttpMethod.GET);
		    allowedMethods.add(HttpMethod.POST);
		    allowedMethods.add(HttpMethod.PUT);
		    
		    router.route().handler(CorsHandler.create(".*.")
		            .allowedHeaders(allowedHeaders)
		            .allowedMethods(allowedMethods));
		  
		  router.get("/").handler(ctx -> {
		        engine.render(ctx.data(), "views/up.hbs")
		            .onSuccess(buffer -> {
		              ctx.response()
		                .putHeader("Content-Type", "text/html")
		                .end(buffer);
		            })
		            .onFailure(ctx::fail);
		        });
		  
		  router.get("/data/getPasswords").handler(this::getAllPasswords);
		  router.get("/data/getPasswords").handler(this::getPassword);
		  doConfig(startPromise, router);
		  
	  }
	  /**
	   * Set up and execute the {@link ConfigRetriever} to load the config for the application
	   * @param start The {@link Promise} which is to be resolved as this Verticle loads
	   * @param router The {@link Router} for the REST API paths
	   */
	  private void doConfig(Promise<Void> start, Router router) {
	  	ConfigStoreOptions defaultconfig = new ConfigStoreOptions()
	   		   .setType("file")
	   		   .setFormat("json")
	   		   .setConfig(new JsonObject().put("path", "config.json"));
	      
	      ConfigStoreOptions cliConfig = new ConfigStoreOptions()
	              .setType("json")
	              .setConfig(config());
	      ConfigRetrieverOptions opts = new ConfigRetrieverOptions()
	   		   .addStore(defaultconfig)
	             .addStore(cliConfig);
	          
	      ConfigRetriever cfgRetr = ConfigRetriever.create(vertx, opts);
	      Handler<AsyncResult<JsonObject>> handler = asyncResult -> this.configHandler(start, router, asyncResult);
	      cfgRetr.getConfig(handler);
	  }
	  /**
	   * 
	   * @param start
	   * @param router
	   * @param asyncResult
	   */
	  void configHandler(Promise<Void> start,Router router, AsyncResult<JsonObject> asyncResult) {
	  	if (asyncResult.succeeded()) {
			   JsonObject config = asyncResult.result();
			   JsonObject http = config.getJsonObject("http");
			   int httpPort = http.getInteger("port");
			   vertx.createHttpServer().requestHandler(router).listen(httpPort);
			   start.complete();
		   }else {
			   start.fail("Unable to load config");
		   }
	  }
	  
	  void getAllPasswords (RoutingContext ctx) {
		  HandlebarsTemplateEngine engine = HandlebarsTemplateEngine.create(vertx);
		  LOGGER.debug("Getting the passwords function");
		  LOGGER.info("Running first command");
		//String command = "powershell.exe  your command";
		  //Getting the version
		  String command = "netsh wlan show profile";
		  // Executing the command
		  Process powerShellProcess = null;
		try {
			powerShellProcess = Runtime.getRuntime().exec(command);
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		  // Getting the results
		  try {
			powerShellProcess.getOutputStream().close();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		  String line;
		  ArrayList<String> SSID = new ArrayList<String>();
		  ArrayList<String> PASS = new ArrayList<String>();
		  JsonObject SSIDPASS = new JsonObject();
		  JsonArray JsonArr = new JsonArray();
		  JsonObject CAPTURE = new JsonObject();
		  //System.out.println("Standard Output:");
		  BufferedReader stdout = new BufferedReader(new InputStreamReader(
		    powerShellProcess.getInputStream()));
		  try {
			while ((line = stdout.readLine()) != null) {
			   
			   Pattern bearerPattern = Pattern.compile(": ([^\"]+)");
			   Matcher matcher = bearerPattern.matcher(line);
			   while (matcher.find())
			   {
				   for (int i = 1; i <= matcher.groupCount(); i++) {
					   //System.out.println("Matched group");
					   //System.out.println(matcher.group(i));
					   Process psPasswordProcess = null;
					   SSID.add(matcher.group(i));
					   LOGGER.debug(matcher.group(i));
					   SSIDPASS.put("SSID", SSID);
					   psPasswordProcess = Runtime.getRuntime().exec("netsh wlan show profile " + matcher.group(i) + " key=clear");
					   psPasswordProcess.getOutputStream().close();
					   String passLine;
						  //System.out.println("Standard Output:");
						  BufferedReader stdoutPass = new BufferedReader(new InputStreamReader(
								  psPasswordProcess.getInputStream()));
						  
							while ((passLine = stdoutPass.readLine()) != null) {
								Pattern keyContent = Pattern.compile("Key Content            : ([^\"]+)");
								Matcher passMatch = keyContent.matcher(passLine);
								while (passMatch.find())
								{
									for (int j = 1; j <= passMatch.groupCount(); j++) {
									System.out.println(passMatch.group(j));
									LOGGER.debug(passMatch.group(j));
									PASS.add(passMatch.group(j));
									SSIDPASS.put("password", PASS);
									
									
								}
									
									
							}
							
				   }
							
			   }
				   
			  }
			   
			}
			JsonArr.add(SSIDPASS);
			} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		  try {
			stdout.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		  System.out.println("Standard Error:");
		  BufferedReader stderr = new BufferedReader(new InputStreamReader(
		    powerShellProcess.getErrorStream()));
		  try {
			while ((line = stderr.readLine()) != null) {
			   System.out.println(line);
			  }
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		  try {
			stderr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  System.out.println(SSIDPASS);
		  System.out.println(JsonArr);
		  CAPTURE.put("captured", SSIDPASS);
			ctx.put("results", JsonArr);
		  engine.render(ctx.data(), "webroot/index.hbs")
		       .onSuccess(buffer -> {
	        	  ctx.response()
	              .putHeader("Content-Type", "text/html")
	              .end(buffer);
	          })
	          .onFailure(ctx::fail);
	  

}
	  void getPassword (RoutingContext ctx) {
	  }
	  }

