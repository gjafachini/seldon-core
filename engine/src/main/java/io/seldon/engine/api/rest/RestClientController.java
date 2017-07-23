package io.seldon.engine.api.rest;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.protobuf.InvalidProtocolBufferException;

import io.seldon.engine.exception.APIException;
import io.seldon.engine.exception.APIException.ApiExceptionType;
import io.seldon.engine.pb.ProtoBufUtils;
import io.seldon.engine.service.PredictionService;
import io.seldon.protos.PredictionProtos.PredictionRequestDef;
import io.seldon.protos.PredictionProtos.PredictionResponseDef;

@RestController
public class RestClientController {
	
	private static Logger logger = LoggerFactory.getLogger(RestClientController.class.getName());
	
	@Autowired
	private PredictionService predictionService;
	
	@RequestMapping("/")
    String home() {
        return "Hello World!";
    }
	
	
	@RequestMapping(value = "/api/v0.1/predictions", method = RequestMethod.POST, consumes = "application/json; charset=utf-8", produces = "application/json; charset=utf-8")
    public ResponseEntity<String> predictions(RequestEntity<String> requestEntity) 
	{
		PredictionRequestDef.Builder builder = PredictionRequestDef.newBuilder();
		try
		{
			ProtoBufUtils.updateMessageBuilderFromJson(builder, requestEntity.getBody());
			PredictionRequestDef request = builder.build();
			PredictionResponseDef response = predictionService.predict(request);
			String json = ProtoBufUtils.toJson(response);
			return new ResponseEntity<String>(json,HttpStatus.OK);
			
		} catch (InvalidProtocolBufferException e) 
		{
			logger.error("Bad request",e);
			throw new APIException(ApiExceptionType.ENGINE_INVALID_JSON,requestEntity.getBody());
		} catch (InterruptedException e) {
			throw new APIException(ApiExceptionType.ENGINE_INTERTUPTED,e.getMessage());
		} catch (ExecutionException e) {
			throw new APIException(ApiExceptionType.ENGINE_EXECUTION_FAILURE,e.getMessage());
		} 
	}
	
	
	/*
	@RequestMapping(value="/api/v0.1/predictions", method = RequestMethod.POST)
    public @ResponseBody
    PredictionServiceReturn predictions(@RequestBody PredictionServiceRequest request, HttpServletRequest req) throws InterruptedException, ExecutionException {

        //TODO: Check authentication here
		
		
		return predictionService.predict(request);
		
    }
    */
	
	@RequestMapping("/api/v0.1/feedback")
    String feedback() {
        return "Hello World!";
    }
	
	@RequestMapping("/api/v0.1/events")
    String events() {
        return "Hello World!";
    }
}
