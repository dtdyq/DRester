package rester.model;

import kong.unirest.UnirestInstance;
import lombok.Data;

import org.apache.velocity.app.VelocityEngine;

@Data
public class HttpContainer {
    private UnirestInstance instance;
    private VelocityEngine engine;
    private Request request;
    private Response response;

    public HttpContainer(UnirestInstance unirestInstance, VelocityEngine engine) {
        this.instance = unirestInstance;
        this.engine = engine;
    }
}
