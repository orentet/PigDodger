package com.pigdodger.misc;

import java.util.Observable;

public class SimpleObservable extends Observable {
	public void changed() {
		this.setChanged();
		this.notifyObservers();
	}

	public void changed(Object obj) {
		this.setChanged();
		this.notifyObservers(obj);
	}
}
