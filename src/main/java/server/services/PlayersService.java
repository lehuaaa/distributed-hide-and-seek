package server.services;

import server.beans.Player;
import server.beans.InitialPlayerInfo;
import server.handlers.PlayersHandler;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("players")
public class PlayersService {

    /* Player initialization */
    @POST
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    public Response addPlayer(Player player){
        InitialPlayerInfo initialPlayerInfo = PlayersHandler.getInstance().addPlayer(player);
        if(initialPlayerInfo != null){
            return Response.ok(initialPlayerInfo).build();
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
