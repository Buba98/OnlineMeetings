function makeCall(method, url, formElement, cback, reset = true) {
	var req = new XMLHttpRequest(); // visible by closure
	req.onreadystatechange = function() {
		cback(req)
	}; // closure
	req.open(method, url);
	if (formElement == null) {
		req.send();
	} else {
		req.send(new FormData(formElement));
	}
	if (formElement !== null && reset === true) {
		formElement.reset();
	}
}

function postJson(url, json, cback) {
	var req = new XMLHttpRequest();
	req.open("POST", url);
	req.setRequestHeader("Content-Type", "application/json");
	req.onreadystatechange = function() {
		cback(req)
	};
	req.send(json);
}

function setCookie(name, value, days) {
	var expires = "";
	if (days) {
		var date = new Date();
		date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
		expires = "; expires=" + date.toUTCString();
	}
	document.cookie = name + "=" + (value || "") + expires + "; path=/";
}
function getCookie(name) {
	var nameEQ = name + "=";
	var ca = document.cookie.split(';');
	for (var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ') c = c.substring(1, c.length);
		if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
	}
	return null;
}
function eraseCookie(name) {
	document.cookie = name + '=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}


Date.prototype.addHoursAndMinutes = function(h, m) {
	this.setTime(this.getTime() + (h * 60 * 60 * 1000) + (m * 60 * 1000));
	return this;
}

Date.prototype.yyyymmddhhMMss = function() {
	var mm = this.getMonth() + 1; // getMonth() is zero-based
	var dd = this.getDate();
	var hh = this.getHours();
	var minutes = this.getMinutes();

	return this.getFullYear() + "-" + (mm > 9 ? '' : '0') + mm + "-" + (dd > 9 ? '' : '0') + dd + " " + (hh > 9 ? '' : '0') + hh + ":" + (minutes > 9 ? '' : '0') + minutes + ":" + 00;
};

