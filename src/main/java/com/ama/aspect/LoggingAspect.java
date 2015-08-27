package com.ama.aspect;

import java.net.InetAddress;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.gson.Gson;

/**
 * @author Love_Taneja
 *
 */
public class LoggingAspect {
	
	/**
	 * logger instance
	 */
	private static final Logger logger = LogManager.getLogger(LoggingAspect.class);
	
	/**
	 * @param joinPoint
	 */
	public void logBefore(JoinPoint joinPoint) {
		try{
			HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
			ThreadContext.put("requestId", InetAddress.getLocalHost().getHostName()+ "_" + System.getProperty("jvmName") + "_" + request.getSession().getId()+ "_" + UUID.randomUUID());			
			logger.log(Level.forName("MONITOR", 200), "Entering Method: " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
		}catch(Exception exc){
			logger.log(Level.ERROR, new Gson().toJson(exc.getStackTrace()));			
		}
	}

	/**
	 * @param joinPoint
	 */
	public void logAfter(JoinPoint joinPoint) {
		try{
			logger.log(Level.forName("MONITOR", 200), "Exiting Method: " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());			
		}catch(Exception exc){
			logger.log(Level.ERROR, new Gson().toJson(exc.getStackTrace()));
		}
	}
	
    /**
     * @param pjp
     * @return Object
     * @throws Throwable
     */
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
    	Gson gson = new Gson();
    	Object output = "";
    	try{
            long start = System.currentTimeMillis();
            Object[] arguments = pjp.getArgs();			
			StringBuffer inputArguments = new StringBuffer();
			for (Object argument: arguments){
				inputArguments.append(gson.toJson(argument)).append(", ");
			}			
            output = pjp.proceed();
            String outputArgument = gson.toJson(output);
            long elapsedTime = System.currentTimeMillis() - start;
            logger.log(Level.forName("MONITOR", 200),"Method Name: " + pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName() +" Input Parameters: "  +  inputArguments.toString() +" Output Parameters: " +  outputArgument +" Time Taken: " +  elapsedTime + " milliseconds.");
    	}catch(Exception exc){
    		logger.log(Level.ERROR, new Gson().toJson(exc.getStackTrace()) );
    	}
        return output;
    }

}
