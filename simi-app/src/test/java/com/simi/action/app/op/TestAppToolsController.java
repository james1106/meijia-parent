package com.simi.action.app.op;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.simi.action.app.JUnitActionBase;

public class TestAppToolsController extends JUnitActionBase{
	
	/*@Test
    public void getChannels() throws Exception {

		String url = "/app/op/get_channels.json";

     	MockHttpServletRequestBuilder postRequest = get(url);

	  
	    ResultActions resultActions = mockMvc.perform(postRequest);

	    resultActions.andExpect(content().contentType(this.mediaType));
	    resultActions.andExpect(status().isOk());


	    System.out.println("RestultActions: " + resultActions.andReturn().getResponse().getContentAsString());

    }		
	
	@Test
    public void getAds() throws Exception {

		String url = "/app/op/get_ads.json?channel_id=1";

     	MockHttpServletRequestBuilder postRequest = get(url);

	  
	    ResultActions resultActions = mockMvc.perform(postRequest);

	    resultActions.andExpect(content().contentType(this.mediaType));
	    resultActions.andExpect(status().isOk());


	    System.out.println("RestultActions: " + resultActions.andReturn().getResponse().getContentAsString());

    }	*/

/*	@Test
    public void getAppTools() throws Exception {

		String url = "/app/op/get_appTools.json?app_type=xcloud";

     	MockHttpServletRequestBuilder postRequest = get(url);

	  
	    ResultActions resultActions = mockMvc.perform(postRequest);

	    resultActions.andExpect(content().contentType(this.mediaType));
	    resultActions.andExpect(status().isOk());


	    System.out.println("RestultActions: " + resultActions.andReturn().getResponse().getContentAsString());

    }*/
	/*@Test
    public void getAppCardType() throws Exception {

		String url = "/app/op/get_appCardType.json?app_type=timechick";

     	MockHttpServletRequestBuilder postRequest = get(url);

	  
	    ResultActions resultActions = mockMvc.perform(postRequest);

	    resultActions.andExpect(content().contentType(this.mediaType));
	    resultActions.andExpect(status().isOk());


	    System.out.println("RestultActions: " + resultActions.andReturn().getResponse().getContentAsString());

    }*/
	
	@Test
    public void testPpostFriendReq() throws Exception {

		String url = "/app/op/user_app_tools.json";
		MockHttpServletRequestBuilder postRequest = post(url);
		postRequest = postRequest.param("user_id", "18");
	    postRequest = postRequest.param("t_id", "50");
	    postRequest = postRequest.param("status", "0");
	    ResultActions resultActions = mockMvc.perform(postRequest);

	    resultActions.andExpect(content().contentType(this.mediaType));
	    resultActions.andExpect(status().isOk());

	    System.out.println("RestultActons: " + resultActions.andReturn().getResponse().getContentAsString());

    }
	
}
