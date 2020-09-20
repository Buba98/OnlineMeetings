package it.polimi.tiw.mi145.riunioniOnline.utils;

public class StringValidation {
	public static boolean isIntValid(String string) {
		try {
			Integer.valueOf(string);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
}
