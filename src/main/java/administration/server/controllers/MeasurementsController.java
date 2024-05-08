package administration.server.controllers;

import administration.server.entities.PlayerMeasurement;
import administration.server.repositories.MeasurementsRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("measurements")
public class MeasurementsController {

    /* Add player's measurement */
    @POST
    @Consumes({"application/json", "application/xml"})
    public Response addPlayerMeasurement(PlayerMeasurement measurement){
        boolean result = MeasurementsRepository.getInstance().addMeasurement(measurement);
        if (result)
            return Response.ok().build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /* Get the average of last n heart rate given a playerId */
    @Path("player-average/{playerId}/{n}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getPlayerAverageOfTheLastNMeasurements(@PathParam("playerId") String playerId, @PathParam("n") int n){
        double result = MeasurementsRepository.getInstance().getPlayerAverage(playerId, n);
        if(result == -1)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(result).build();
    }

    /* Get the average of the measurements occurred between timestamp t1 and timestamp t2 */
    @Path("interval-average/{t1}/{t2}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getAverageOfTheMeasurementsBetweenT1AndT2(@PathParam("t1") long t1, @PathParam("t2") long t2){
        return Response.ok(MeasurementsRepository.getInstance().getIntervalAverage(t1, t2)).build();
    }
}