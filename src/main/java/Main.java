import java.util.ArrayList;

import request.handler.Challenge10RequestHandler;
import request.handler.Challenge11RequestHandler;
import request.handler.ChallengeRequestHandler;


public class Main {

	public static void main(String[] args) {
		
		
		ArrayList<ChallengeRequestHandler> handlers=new ArrayList<ChallengeRequestHandler>();
		
		handlers.add(new Challenge10RequestHandler());
		handlers.add(new Challenge11RequestHandler());
		
		if(args.length>1)
		{
			boolean taskHandled=false;
			for(ChallengeRequestHandler handler:handlers)
			{
				if(handler.shouldProcess(args[0]))
				{
					handler.process(args);
					taskHandled=true;
					break;
				}
			}
			if(!taskHandled)
			{
				System.out.println("Challenge ID is not recognised.");
			}
		}
		
	}

}
