// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package statelessservice;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import microsoft.servicefabric.services.communication.runtime.CommunicationListener;
import microsoft.servicefabric.services.runtime.StatelessServiceContext;
import system.fabric.CancellationToken;

public class HttpCommunicationListener implements CommunicationListener {    
    
    private StatelessServiceContext context;
    private final int port;
    private Server server; 
    
	private static final Logger logger = Logger.getLogger(HttpCommunicationListener.class.getName());

    public HttpCommunicationListener(StatelessServiceContext context, int port) {
        this.context = context;
        this.port = port;
    }
    
    	public static class HelloServlet extends HttpServlet 
    	{
    		protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    		{
    			response.setContentType("text/html");
    			response.setStatus(HttpServletResponse.SC_OK);
    			response.getWriter().println("<h1>New Hello Simple Servlet</h1>"); 
        } 
    }

    @Override
    public CompletableFuture<String> openAsync(CancellationToken cancellationToken) {
	    	server = new Server(this.port);
	    	logger.log(Level.INFO, "Port is: " +this.port);
	    	try {
	    		ServletHandler servletHandler = new ServletHandler();
	    		server.setHandler(servletHandler);
	    				
	    		servletHandler.addServletWithMapping(HelloServlet.class, "/");
	    		
	    		server.start();
	    		server.join();
	    	} catch (Exception e) {          
	    		e.printStackTrace();
	    	} 
	    	String publishUri = String.format("http://%s:%d/", this.context.getNodeContext().getIpAddressOrFQDN(), port);
        return CompletableFuture.completedFuture(publishUri);
    }

    @Override
    public CompletableFuture<?> closeAsync(CancellationToken cancellationToken) {
    		logger.log(Level.INFO, "Close Async");
    		
    		CompletableFuture cf = CompletableFuture.runAsync(() -> {
    			try {
				server.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
    		});
        return CompletableFuture.completedFuture(true);
    }

	@Override
	public void abort() {
		logger.log(Level.INFO, "Abort for port: "+ this.port);
		try {
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
};