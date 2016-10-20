package com.rocket.vitalis.services;

import com.rocket.vitalis.model.*;
import com.rocket.vitalis.repositories.MeasurementRepository;
import com.rocket.vitalis.repositories.MonitoringRepository;
import com.rocket.vitalis.repositories.SensorRepository;
import com.rocket.vitalis.repositories.UserRepository;
import com.rocket.vitalis.utils.PBKDF2Service;
import com.rocket.vitalis.utils.VitalisUtils;
import lombok.extern.log4j.Log4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.rocket.vitalis.model.MeasurementType.*;
import static com.rocket.vitalis.utils.PBKDF2Service.createHash;
import static com.rocket.vitalis.utils.VitalisUtils.randomDouble;
import static com.rocket.vitalis.utils.VitalisUtils.randomInteger;
import static org.joda.time.DateTime.now;

/**
 * Created by sscotti on 10/9/16.
 */
@Service
@Profile("dev")
@Log4j
public class WarmupService {

    @Autowired  private UserRepository          userRepository;
    @Autowired  private MonitoringRepository    monitoringRepository;
    @Autowired  private SensorRepository        sensorRepository;
    @Autowired  private MeasurementRepository   measurementRepository;

    public void initApplicationData(){

        User sebas = registerUser("sebastians@vitalis.com", "1234", "Sebastian Scotti", "https://fbcdn-sphotos-e-a.akamaihd.net/hphotos-ak-xtp1/v/t1.0-9/13321745_10154178340059144_6375953951169924641_n.jpg?oh=d947fda977c1f25690f6ebe18721c2c7&oe=58616839&__gda__=1483568380_2f73224ebfe735922bd2bb150593bcf5");
        User ailin = registerUser("ailin@vitalis.com", "1234", "Ailin Merlo", "https://scontent-gru2-1.xx.fbcdn.net/v/t1.0-9/5347_1054019857995682_3889779951510681334_n.jpg?oh=3a08d9f2ea9e8ce5c5c025e376a3c752&oe=589E5CA5");
        User sebap = registerUser("sebastianp@vitalis.com", "1234", "Sebastian Pantuso", "https://fbcdn-sphotos-g-a.akamaihd.net/hphotos-ak-xft1/v/t1.0-9/11045288_10206029105462702_4584767361088017524_n.jpg?oh=05232c2e3bb83c6e08443c91e00902c6&oe=58A7F485&__gda__=1483445623_bd6c8cce73d997c97eb88af39a67a150");
        User aldo = registerUser("aldo@vitalis.com", "1234", "Aldo Flores", "https://fbcdn-sphotos-a-a.akamaihd.net/hphotos-ak-xpa1/v/t1.0-9/1176314_10151820549257173_1914444078_n.jpg?oh=1b92f3f3880769d072f292876e7b894b&oe=58AB9BD1&__gda__=1483678478_b1cd8491dd634ca481de95148719402c");
        User mariano = registerUser("mariano@vitalis.com", "1234", "Mariano Ramirez", "http://cdn.images.express.co.uk/img/dynamic/67/590x/Ramiro-Funes-Mori-599538.jpg");

        User sancho = registerUser("sancho@vitalis.com", "1234", "Sancho Panza", "https://fbcdn-sphotos-a-a.akamaihd.net/hphotos-ak-xlf1/v/t1.0-9/14611023_10154547018059144_1423955428690896778_n.jpg?oh=bb89db306ebc7c4adea5dcbe2298a149&oe=586C74AC&__gda__=1487668243_e7c6ae65113c49801bc756274701ffdb");
        sancho.getFollowing().add(sebas);
        sancho.getFollowing().add(ailin);
        sancho.getFollowing().add(sebap);
        sancho.getFollowing().add(aldo);
        sancho.getFollowing().add(mariano);

        userRepository.save(sancho);

        Monitoring monitoring = createMonitoring(sebas);

        createMeasurements(monitoring);

    }

    private Monitoring createMonitoring(User sebas) {
        Monitoring monitoring = new Monitoring();

        Collection<Sensor> sensors = new ArrayList<>(values().length);

        for(MeasurementType measurementType : values()){
            sensors.add(createSensor(measurementType));
        }

        sensorRepository.save(sensors);

        monitoring.setSensors(sensors);
        monitoring.setPatient(sebas);

        monitoring = monitoringRepository.save(monitoring);
        return monitoring;
    }

    private void createMeasurements(Monitoring monitoring) {
        DateTime time = now().minusMinutes(16);
        DateTime now = now();

        Collection<Measurement> measurements = new ArrayList<>(45);

        Map<MeasurementType, Double> lastValues             = new HashMap<>(3);
        Map<MeasurementType, Double> lastValuesSecondary    = new HashMap<>(3);

        while(time.plusMinutes(1).isBefore(now)) {

            double nextTemperature          = randomDouble(37d, 38d);
            double nextBloodOxygen          = randomDouble(98.5d, 99d);
            double nextRespiratoryRate      = (double) randomInteger(15, 18);
            double nextHeartRate            = (double) randomInteger(60, 100);

            double nextSystolicPressure     = (double) randomInteger(90, 119);
            double nextDiastolicPressure    = (double) randomInteger(60, 79);

            lastValues.put(TEMPERATURE, nextTemperature);
            lastValues.put(BLOOD_OXYGEN, nextBloodOxygen);
            lastValues.put(RESPIRATORY_RATE, nextRespiratoryRate);
            lastValues.put(HEART_RATE, nextHeartRate);
            lastValues.put(HEART_RATE, nextHeartRate);
            lastValues.put(BLOOD_PRESSURE, nextSystolicPressure);

            lastValuesSecondary.put(BLOOD_PRESSURE, nextDiastolicPressure);

//            lastValues.put(SYSTOLIC_PRESSURE, nextSystolicPressure);
//            lastValues.put(DIASTOLIC_PRESSURE, nextDiastolicPressure);

            measurements.add(new Measurement(monitoring, time.toDate(), TEMPERATURE, nextTemperature));
            measurements.add(new Measurement(monitoring, time.toDate(), BLOOD_OXYGEN, nextBloodOxygen));
            measurements.add(new Measurement(monitoring, time.toDate(), RESPIRATORY_RATE, nextRespiratoryRate));
            measurements.add(new Measurement(monitoring, time.toDate(), HEART_RATE, nextHeartRate));
            measurements.add(new Measurement(monitoring, time.toDate(), BLOOD_PRESSURE, nextSystolicPressure, nextDiastolicPressure));
//            measurements.add(new Measurement(monitoring, time.toDate(), SYSTOLIC_PRESSURE, nextSystolicPressure));
//            measurements.add(new Measurement(monitoring, time.toDate(), DIASTOLIC_PRESSURE, nextDiastolicPressure));

            time = time.plusMinutes(1);
        }

        final Date lastMonitoringDate = time.toDate();
        monitoring.getSensors().forEach(sensor -> {
            sensor.setLastMonitoringDate(lastMonitoringDate);
            sensor.setLastValue(lastValues.get(sensor.getMeasurementType()));
            sensor.setLastValueSecondary(lastValuesSecondary.get(sensor.getMeasurementType()));
        });

        monitoringRepository.save(monitoring);
        measurementRepository.save(measurements);
    }

    private Sensor createSensor(MeasurementType type) {
        Sensor temperatureSensor = new Sensor();
        temperatureSensor.setMeasurementType(type);
        temperatureSensor.setStatus(SensorStatus.ENABLED);

        return temperatureSensor;
    }

    public User registerUser(String email, String password, String name, String pictureUrl){

        try {
            String hash = createHash(password);

            User user = new User(email, hash);
            user.setName(name);
            user.setPictureUrl(pictureUrl);

            return userRepository.save(user);

        } catch (PBKDF2Service.CannotPerformOperationException e) {
            throw new IllegalStateException(e);
        }

    }
}
