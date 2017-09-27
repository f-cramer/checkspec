package hospital.patients;

import java.time.LocalTime;

public class EmergencyPatient extends AbstractPatient {

	public EmergencyPatient(String patientName, LocalTime patientArrivalTime) {
		super(patientName, patientArrivalTime);
		// implementation
	}

	@Override
	public int compareTo(AbstractPatient o) {
		return 0;
		// implementation
	}

	@Override
	public TreatmentPriority getPriority() {
		return null;
	}

	@Override
	public String getCatInfo() {
		return "";
	}
}
