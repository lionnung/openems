package io.openems.edge.controller.symmetric.dynamiccharge;

import java.time.Instant;
import java.util.Arrays;

import org.junit.Test;

import io.openems.common.types.ChannelAddress;
import io.openems.edge.common.test.AbstractComponentTest.TestCase;
import io.openems.edge.common.test.DummyComponentManager;
import io.openems.edge.common.test.DummyConfigurationAdmin;
import io.openems.edge.common.test.TimeLeapClock;
import io.openems.edge.controller.test.ControllerTest;
import io.openems.edge.ess.test.DummyManagedSymmetricEss;
import io.openems.edge.meter.test.DummyAsymmetricMeter;
import io.openems.edge.predictor.api.test.DummyPrediction48Hours;
import io.openems.edge.predictor.api.test.DummyPredictor24Hours;
import io.openems.edge.predictor.api.test.DummyPredictorManager;

public class MyControllerTest {
	
	// Ids
		private static final String CTRL_ID = "ctrlDynamicCharge0";
		private static final String PREDICTOR_ID = "predictor0";
		private static final String ESS_ID = "ess0";
		private static final String METER_ID = "meter0";

		// Components
		private static final DummyManagedSymmetricEss ESS = new DummyManagedSymmetricEss(ESS_ID);
		private static final DummyAsymmetricMeter METER = new DummyAsymmetricMeter(METER_ID);

		// Ess channels
		private static final ChannelAddress ESS_CAPACITY = new ChannelAddress(ESS_ID, "Capacity");
		private static final ChannelAddress ESS_SOC = new ChannelAddress(ESS_ID, "Soc");
		private static final ChannelAddress ESS_MAX_APPARENT_POWER = new ChannelAddress(ESS_ID, "MaxApparentPower");
		
		// Meter channels
		private static final ChannelAddress METER_ACTIVE_POWER = new ChannelAddress(METER_ID, "ActivePower");
		
		/*
		 * Default Prediction values
		 */
		private static final Integer[] DEFAULT_PRODUCTION_PREDICTION = {
				/* 00:00-03:450 */
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //
				/* 04:00-07:45 */
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 74, 297, 610, //
				/* 08:00-11:45 */
				913, 1399, 1838, 2261, 2662, 3052, 3405, 3708, 4011, 4270, 4458, 4630, 4794, 4908, 4963, 4960, //
				/* 12:00-15:45 */
				4973, 4940, 4859, 4807, 4698, 4530, 4348, 4147, 3527, 3141, 2917, 2703, 2484, 2233, 1971, 1674, //
				/* 16:00-19:45 */
				1386, 1089, 811, 531, 298, 143, 41, 0, 0, 0, 0, 0, 0, 0, 0, 0, //
				/* 20:00-23:45 */
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //
				/* 00:00-03:45 */
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //
				/* 04:00-07:45 */
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 130, 402, 667, //
				/* 08:00-11:45 */
				1023, 1631, 2020, 2420, 2834, 3237, 3638, 4006, 4338, 4597, 4825, 4965, 5111, 5213, 5268, 5317, //
				/* 12:00-15:45 */
				5321, 5271, 5232, 5193, 5044, 4915, 4738, 4499, 3702, 3226, 3046, 2857, 2649, 2421, 2184, 1933, //
				/* 16:00-19:45 */
				1674, 1364, 1070, 754, 447, 193, 52, 0, 0, 0, 0, 0, 0, 0, 0, 0, //
				/* 20:00-23:45 */
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 //
		};

		private static final Integer[] DEFAULT_CONSUMPTION_PREDICTION = {
				/* 00:00-03:450 */
				1021, 1208, 713, 931, 2847, 2551, 1558, 1234, 433, 633, 1355, 606, 430, 1432, 1121, 502, //
				/* 04:00-07:45 */
				294, 1048, 1194, 914, 1534, 1226, 1235, 977, 578, 1253, 1983, 1417, 513, 929, 1102, 445, //
				/* 08:00-11:45 */
				1208, 2791, 2729, 2609, 2086, 1454, 848, 816, 2610, 3150, 2036, 1180, 359, 1316, 3447, 2104, //
				/* 12:00-15:45 */
				905, 802, 828, 812, 863, 633, 293, 379, 296, 296, 436, 140, 135, 196, 230, 175, //
				/* 16:00-19:45 */
				365, 758, 325, 264, 181, 167, 228, 1082, 777, 417, 798, 1268, 409, 830, 1191, 417, //
				/* 20:00-23:45 */
				1087, 2958, 2946, 2235, 1343, 483, 796, 1201, 567, 395, 989, 1066, 370, 989, 1255, 660, //
				/* 00:00-03:45 */
				349, 880, 1186, 580, 327, 911, 1135, 553, 265, 938, 1165, 567, 278, 863, 1239, 658, //
				/* 04:00-07:45 */
				236, 816, 1173, 1131, 498, 550, 1344, 1226, 874, 504, 1733, 1809, 1576, 369, 771, 2583, //
				/* 08:00-11:45 */
				3202, 2174, 1878, 2132, 2109, 1895, 1565, 1477, 1613, 1716, 1867, 1726, 1700, 1787, 1755, 1734, //
				/* 12:00-15:45 */
				1380, 691, 338, 168, 199, 448, 662, 205, 183, 70, 169, 276, 149, 76, 195, 168, //
				/* 16:00-19:45 */
				159, 266, 135, 120, 224, 979, 2965, 1337, 1116, 795, 334, 390, 433, 369, 762, 2908, //
				/* 20:00-23:45 */
				3226, 2358, 1778, 1002, 455, 654, 534, 1587, 1638, 459, 330, 258, 368, 728, 1096, 878 //
		};

	@Test
	private void test() throws Exception {
		
		final TimeLeapClock clock = new TimeLeapClock(Instant.parse("2020-01-01T12:00:00.00Z"), ZoneOffset.UTC);
		final DummyComponentManager cm = new DummyComponentManager(clock);

		// Predictions
		final DummyPrediction48Hours productionPrediction = new DummyPrediction48Hours(DEFAULT_PRODUCTION_PREDICTION);
		final DummyPrediction48Hours consumptionPrediction = new DummyPrediction48Hours(DEFAULT_CONSUMPTION_PREDICTION);

		// Predictors
		final DummyPredictor24Hours productionPredictor = new DummyPredictor24Hours(PREDICTOR_ID, cm,
				productionPrediction, "_sum/ProductionActivePower");
		final DummyPredictor24Hours consumptionPredictor = new DummyPredictor24Hours(PREDICTOR_ID, cm,
				consumptionPrediction, "_sum/ConsumptionActivePower");

		// PredictorManager
		final DummyPredictorManager predictorManager = new DummyPredictorManager(productionPredictor,
				consumptionPredictor);

		System.out.println(Arrays.toString(predictorManager
				.get24HoursPrediction(ChannelAddress.fromString("_sum/ProductionActivePower")).getValues()));
		System.out.println(Arrays.toString(predictorManager
				.get24HoursPrediction(ChannelAddress.fromString("_sum/ConsumptionActivePower")).getValues()));
		
		new ControllerTest(new DynamicCharge()) //
		.addReference("predictorManager", predictorManager) //
		.addReference("componentManager", cm) //
		.addReference("cm", new DummyConfigurationAdmin()) //
		.addReference("ess", ESS) //
		.addReference("meter", METER) //
		.activate(MyConfig.create() //
				.setEssId(ESS_ID) //
				.setId(CTRL_ID) //
				.setMaximumSellToGridPower(7_000) //
				.setMeterId(METER_ID) //
				.setNoOfBufferMinutes(120) //
				.setPowerBuffer(0) //
				.build()) //
				.next(new TestCase());
	}

}