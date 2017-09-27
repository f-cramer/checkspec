package hospital.patients;

import java.time.LocalTime;

public abstract class AbstractPatient implements Comparable<AbstractPatient> {

	protected LocalTime arrivalTime;

	public AbstractPatient(String patientName, LocalTime patientArrivalTime) {
		// implementation
	}

	public abstract TreatmentPriority getPriority();

	public abstract String getCatInfo();

	@Override
	public String toString() {
		return "";
		// implementation
	}
}
