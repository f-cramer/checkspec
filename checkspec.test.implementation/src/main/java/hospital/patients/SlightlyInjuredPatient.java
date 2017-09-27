package hospital.patients;

import java.time.LocalTime;

public class SlightlyInjuredPatient extends AbstractPatient {

	public SlightlyInjuredPatient(String patientName, LocalTime patientArrivalTime) {
		super(patientName, patientArrivalTime);
	}

	@Override
	public int compareTo(AbstractPatient o) {
		return 0;
		// implementation
	}

	@Override
	public TreatmentPriority getPriority() {
		return null;
		// implementation
	}

	@Override
	public String getCatInfo() {
		return "";
		// implementation
	}
}
