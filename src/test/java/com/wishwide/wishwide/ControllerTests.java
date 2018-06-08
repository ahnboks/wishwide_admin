package com.wishwide.wishwide;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.*;
import lombok.extern.java.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class ControllerTests {



    @Test
    public void geoCoderTest(){
        String location = "인천 부평구 산곡2동 안남로 231";

        Geocoder geocoder = new Geocoder();

// setAddress : 변환하려는 주소 (경기도 성남시 분당구 등)

// setLanguate : 인코딩 설정

        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(location).setLanguage("ko").getGeocoderRequest();

        GeocodeResponse geocoderResponse;
        Float[] coords = new Float[2];

        try {

            geocoderResponse = geocoder.geocode(geocoderRequest);

            if (geocoderResponse.getStatus() == GeocoderStatus.OK & !geocoderResponse.getResults().isEmpty()) {

                GeocoderResult geocoderResult=geocoderResponse.getResults().iterator().next();

                LatLng latitudeLongitude = geocoderResult.getGeometry().getLocation();



                coords[0] = latitudeLongitude.getLat().floatValue();

                coords[1] = latitudeLongitude.getLng().floatValue();

                log.info("위도경도1 : "+coords[0]+coords[1]);
            }
            log.info("위도경도2 : "+coords[0]+coords[1]);
        } catch (IOException ex) {

            ex.printStackTrace();

        }
        log.info("위도경도3 : "+coords[0]+coords[1]);
    }
}
