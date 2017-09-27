package checkspec.test;

/*-
 * #%L
 * CheckSpec Test
 * %%
 * Copyright (C) 2017 Florian Cramer
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.Test;

import checkspec.cli.CommandLineException;
import checkspec.report.ClassReport;
import checkspec.report.SpecReport;

public class HospitalTest extends AbstractIntegrationTest {

	private static final String ARRAY_LIST_HEAP = "hospital.heap.ArrayListHeap";
	private static final String EMERGENCY_PATIENT = "hospital.patients.EmergencyPatient";
	private static final String SEVERELY_INJURED_PATIENT = "hospital.patients.SeverelyInjuredPatient";
	private static final String SLIGHTLY_INJURED_PATIENT = "hospital.patients.SlightlyInjuredPatient";
	private static final String PATIENT_QUEUE = "hospital.PatientQueue";

	@Test
	public void hospitalTest() throws CommandLineException {
		String[] specClassNames = { ARRAY_LIST_HEAP, EMERGENCY_PATIENT, SEVERELY_INJURED_PATIENT,
				SLIGHTLY_INJURED_PATIENT, PATIENT_QUEUE };
		SpecReport[] reports = generateReports("hospital", specClassNames);

		assertThat(reports).hasSize(5);

		List<ClassReport> arrayListHeapReports = findClassReportsForSpecificationClass(reports, ARRAY_LIST_HEAP);
		assertThat(arrayListHeapReports).hasSize(2);
		assertThat(getNameOfBestImplementation(arrayListHeapReports)).isEqualTo(ARRAY_LIST_HEAP);

		List<ClassReport> emergencyPatientReports = findClassReportsForSpecificationClass(reports, EMERGENCY_PATIENT);
		assertThat(emergencyPatientReports).hasSize(4);
		assertThat(getNameOfBestImplementation(emergencyPatientReports)).isEqualTo(EMERGENCY_PATIENT);

		List<ClassReport> severelyInjuredPatientReports = findClassReportsForSpecificationClass(reports, SEVERELY_INJURED_PATIENT);
		assertThat(severelyInjuredPatientReports).hasSize(4);
		assertThat(getNameOfBestImplementation(severelyInjuredPatientReports)).isEqualTo(SEVERELY_INJURED_PATIENT);

		List<ClassReport> slightlyInjuredPatientReports = findClassReportsForSpecificationClass(reports, SLIGHTLY_INJURED_PATIENT);
		assertThat(slightlyInjuredPatientReports).hasSize(4);
		assertThat(getNameOfBestImplementation(slightlyInjuredPatientReports)).isEqualTo(SLIGHTLY_INJURED_PATIENT);

		List<ClassReport> patientQueueReports = findClassReportsForSpecificationClass(reports, PATIENT_QUEUE);
		assertThat(patientQueueReports).hasSize(1);
		assertThat(getNameOfBestImplementation(patientQueueReports)).isEqualTo(PATIENT_QUEUE);
	}
}
