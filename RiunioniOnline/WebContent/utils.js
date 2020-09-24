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
	var mm = this.getMonth() + 1;
	var dd = this.getDate();
	var hh = this.getHours();
	var minutes = this.getMinutes();

	return this.getFullYear() + "-" + (mm > 9 ? '' : '0') + mm + "-" + (dd > 9 ? '' : '0') + dd + " " + (hh > 9 ? '' : '0') + hh + ":" + (minutes > 9 ? '' : '0') + minutes + ":00";
};

function msToTime(duration) {
	var milliseconds = parseInt((duration % 1000) / 100),
		seconds = Math.floor((duration / 1000) % 60),
		minutes = Math.floor((duration / (1000 * 60)) % 60),
		hours = Math.floor((duration / (1000 * 60 * 60)) % 24);

	hours = (hours < 10) ? "0" + hours : hours;
	minutes = (minutes < 10) ? "0" + minutes : minutes;
	seconds = (seconds < 10) ? "0" + seconds : seconds;

	return hours + ":" + minutes + ":" + seconds + "." + milliseconds;
}

function differenceDates(datestart, dateend) {
	var diffMs = (dateend - datestart);
	var diffDays = Math.floor(diffMs / 86400000);
	var diffHrs = Math.floor((diffMs % 86400000) / 3600000);
	var diffMins = Math.round(((diffMs % 86400000) % 3600000) / 60000);
	return ((diffDays > 0) ? diffDays + " days " : "") + ((diffHrs > 0) ? diffHrs + " hours " : "") + ((diffMins > 0) ? diffMins + " minutes" : "");
}