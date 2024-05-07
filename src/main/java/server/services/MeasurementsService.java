package server.services;

import server.managers.MeasurementsManager;
import server.beans.PlayerMeasurement;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("measurements")
public class MeasurementsService {

    /* Add player's measurement */
    @POST
    @Consumes({"application/json", "application/xml"})
    public Response addPlayerMeasurement(PlayerMeasurement measurement){
        MeasurementsManager.getInstance().addMeasurement(measurement);
        return Response.ok().build();
    }

    /* Get the average of last n heart rate given a playerId */
    @Path("player-average/{playerId}/{n}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getAverageOfTheLastNMeasurementByPlayerId(@PathParam("playerId") String playerId, @PathParam("n") int n){
        double result = MeasurementsManager.getInstance().getAverageOfLastNMeasurementsById(playerId, n);
        if(result == -1)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(result).build();
    }

    /* Get the average of the measurements occurred between timestamp t1 and timestamp t2 */
    @Path("interval-average/{t1}/{t2}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getAverageOfTheLastNMeasurementByPlayerId(@PathParam("t1") long t1, @PathParam("t2") long t2){
        // TODO
        return Response.ok(12).build();
    }
}