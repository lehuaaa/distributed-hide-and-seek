package administration.server.services;

import administration.server.handlers.PlayersHandler;
import administration.server.beans.Player;
import administration.server.beans.MatchInfo;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("players")
public class PlayersService {

    /* Add player into the match */
    @POST
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    public Response addPlayer(Player player){
        MatchInfo matchInfo = PlayersHandler.getInstance().addPlayer(player);
        if(matchInfo != null){
            return Response.ok(matchInfo).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /* Get the list of players currently in the game */
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getPlayersList(){
        return Response.ok(PlayersHandler.getInstance()).build();
    }
}