package server.services;

import server.beans.PlayerInfo;
import server.beans.InitialInfo;
import server.managers.PlayersManager;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("players")
public class PlayersService {

    /* Player initialization */
    @POST
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    public Response addPlayerToTheGame(PlayerInfo player){
        InitialInfo initialInfo = PlayersManager.getInstance().addPlayer(player);
        if(initialInfo != null){
            return Response.ok(initialInfo).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /* Get the list of players currently in the game */
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getPlayersList(){
        return Response.ok(PlayersManager.getInstance()).build();
    }
}
