package io.openems.edge.ess.mr.gridcon.writewords;

public class CosPhiParameters {

	public static final int COS_PHI_ADDRESS = 32752;

	public CosPhiParameters() {

	}

	private float cosPhiSetPoint1 = 1f;
	private float cosPhiSetPoint2 = 1f;

	public float getCosPhiSetPoint1() {
		return cosPhiSetPoint1;
	}

	public void setCosPhiSetPoint1(float cosPhiSetPoint1) {
		this.cosPhiSetPoint1 = cosPhiSetPoint1;
	}

	public float getCosPhiSetPoint2() {
		return cosPhiSetPoint2;
	}

	public void setCosPhiSetPoint2(float cosPhiSetPoint2) {
		this.cosPhiSetPoint2 = cosPhiSetPoint2;
	}

	@Override
	public String toString() {
		return "CosPhiParameters [cosPhiSetPoint1=" + cosPhiSetPoint1 + ", cosPhiSetPoint2=" + cosPhiSetPoint2 + "]\n" + getHexRepresentation();
	}

	private String getHexRepresentation() {
		StringBuilder sb = new StringBuilder();
		sb.append(COS_PHI_ADDRESS);
		sb.append(": ");
		sb.append(HexFormatter.format(cosPhiSetPoint1, true));
		sb.append("  ");
		sb.append(HexFormatter.format(cosPhiSetPoint2, true));

		return sb.toString();
	}
}
