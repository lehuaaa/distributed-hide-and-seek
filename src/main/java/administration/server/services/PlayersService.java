package administration.server.services;

import administration.server.repositories.PlayersRepository;
import administration.server.beans.Node;
import administration.server.beans.MatchInfo;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("players")
public class PlayersService {

    /* Add player into the match */
    @POST
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    public Response addPlayer(Node player){
        MatchInfo matchInfo = PlayersRepository.getInstance().addPlayer(player);
        if(matchInfo != null){
            return Response.ok(matchInfo).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /* Get the list of players currently in the game */
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getPlayersList(){
        return Response.ok(PlayersRepository.getInstance()).build();
    }
}