(function() {
	var meetingData = null;

	window.addEventListener("load", () => {
		if (getCookie("session") == "" || getCookie("session") == null) {
			window.location.href = "index.html";
		} else {
			pageHandler();
		}
	}, false);



	function MeetingData() {
		var row, name, date, expirationDate, input, lable;
		var self = this;


		this.newMeeting = ({
			name: null,
			date: null,
			expirationDate: null,
			participants: []
		});

		this.data = ({
			retry: 0,
			alert: document.getElementById("id_alert"),
			logout: document.getElementById("id_logout"),
			ownMeetings: document.getElementById("id_ownMeetingsTable"),
			otherMeetings: document.getElementById("id_otherMeetingsTable"),
			username: document.getElementById("id_username"),
			participantsModal: document.getElementById("id_newMeetingParticipantsModal"),
			participantsInput: document.getElementById("id_newMeetingParticipantsCheckBoxs"),
			formNewMeeting: document.getElementById("id_newMeeting"),
			buttonSelectParticipants: document.getElementById("id_goToParticipantsModal"),
			buttonSendNewMeeting: document.getElementById("id_sendNewMeeting"),
			maxParticipants: document.getElementById("maxParticipants")
		});
		this.loadContent = function(json) {

			this.data.logout.addEventListener('click', () => {
				self.logout();
			});

			this.data.buttonSelectParticipants.addEventListener('click', (e) => {

				var form = e.target.closest("form");

				if (form.checkValidity()) {
					if (form.expirationHours.value > 0 || form.expirationMinutes.value > 0) {
						var expirationDate = new Date(form.date.value + " " + form.hourAndMinutes.value + ":00").addHoursAndMinutes(form.expirationHours.value, form.expirationMinutes.value);

						if (expirationDate - new Date() < 0) {
							self.alert("Meeting already expired");
						} else {
							this.newMeeting.name = form.name.value;
							this.newMeeting.date = form.date.value + " " + form.hourAndMinutes.value + ":00";
							this.newMeeting.expirationDate = expirationDate.yyyymmddhhMMss();
							this.data.participantsModal.style.display = "block";

							form.name.value = "";
							form.date.value = "";
							form.hourAndMinutes.value = "";
							form.expirationHours.value = 0;
							form.expirationMinutes.value = 0;
							form.maxParticipants.value = 1;
						}
					}
					else {
						self.alert("Meeting must last at least one minute");
					}

				} else {
					form.reportValidity();
				}
			}, false);

			self.data.buttonSendNewMeeting.addEventListener('click', () => {

				self.newMeeting.participants = [];

				document.getElementById("id_alert_newMeeting").innerText = "";

				var chk_arr = document.getElementsByName("checkbox[]");

				for (k = 0; k < chk_arr.length; k++) {
					if (chk_arr[k].checked) {
						self.newMeeting.participants.push(chk_arr[k].value);
					}
				}

				if (self.newMeeting.participants.length > 0) {
					if (self.data.maxParticipants.value >= self.newMeeting.participants.length) {
						postJson('MeetingHandler', JSON.stringify(this.newMeeting),
							function(req) {
								if (req.readyState == XMLHttpRequest.DONE) {
									var message = req.responseText;
									switch (req.status) {
										case 200:
											self.data.participantsModal.style.display = "none";
											self.updateNewMeeting();
											break;
										case 401:
											this.logout();
											break;
									}
									self.alert(message);
								}
							}
						);
					} else {
						self.data.retry++;
						if (self.data.retry < 3) {
							document.getElementById("id_alert_newMeeting").innerText = "Select at most " + self.data.maxParticipants.value + " participant"
						} else {
							self.alert("Too many failed attempts");
							self.data.participantsModal.style.display = "none";
							self.reset();
						}
					}

				} else {
					document.getElementById("id_alert_newMeeting").innerText = "Select at least 1 participant"
				}
			});



			window.addEventListener('click', (e) => {
				if (e.target == this.data.participantsModal) {
					this.data.participantsModal.style.display = "none";
					self.reset();
				}
			}, false);

			this.data.username.textContent = json.userName;

			json.ownMeetings.forEach(function(meeting) {
				row = document.createElement("tr");

				name = document.createElement("td");
				name.textContent = meeting.name;

				date = document.createElement("td");
				date.textContent = meeting.date;

				expirationDate = document.createElement("td");
				expirationDate.textContent = differenceDates(Date.parse(meeting.date.replace(" ", "T")), Date.parse(meeting.expirationDate.replace(" ", "T")));

				row.appendChild(name);
				row.appendChild(date);
				row.appendChild(expirationDate);

				self.data.ownMeetings.appendChild(row);
			});

			json.otherMeetings.forEach(function(meeting) {
				row = document.createElement("tr");

				name = document.createElement("td");
				name.textContent = meeting.name;

				date = document.createElement("td");
				date.textContent = meeting.date;

				expirationDate = document.createElement("td");
				expirationDate.textContent = differenceDates(Date.parse(meeting.date.replace(" ", "T")), Date.parse(meeting.expirationDate.replace(" ", "T")));

				row.appendChild(name);
				row.appendChild(date);
				row.appendChild(expirationDate);

				self.data.otherMeetings.appendChild(row);
			});

			json.idsAndNames.forEach(function(idAndName) {
				if (idAndName.id != getCookie("person_id")) {
					input = document.createElement("input");
					input.setAttribute("type", "checkbox");
					input.setAttribute("value", idAndName.id);
					input.setAttribute("id", idAndName.id);
					input.setAttribute("name", "checkbox[]");

					lable = document.createElement("lable");
					lable.setAttribute("for", idAndName.id)
					lable.innerText = idAndName.username;


					self.data.participantsInput.appendChild(input);
					self.data.participantsInput.appendChild(lable);

					self.data.participantsInput.appendChild(document.createElement("br"));
				}
			});

		};

		this.alert = function(message) {
			this.data.alert.textContent = message;
		};

		this.logout = function() {
			eraseCookie("person_id");
			window.location.href = "index.html";
		};

		this.reset = function() {
			document.getElementById("id_alert_newMeeting").innerText = "";

			var chk_arr = document.getElementsByName("checkbox[]");
			for (k = 0; k < chk_arr.length; k++) {
				chk_arr[k].checked = false;
			}

			this.data.retry = 0;
		};

		this.updateNewMeeting = function() {
			row = document.createElement("tr");

			name = document.createElement("td");
			name.textContent = this.newMeeting.name;

			date = document.createElement("td");
			date.textContent = this.newMeeting.date;

			expirationDate = document.createElement("td");
			expirationDate.textContent = differenceDates(Date.parse(this.newMeeting.date.replace(" ", "T")), Date.parse(this.newMeeting.expirationDate.replace(" ", "T")));

			row.appendChild(name);
			row.appendChild(date);
			row.appendChild(expirationDate);

			this.data.ownMeetings.appendChild(row);

			this.newMeeting = ({
				name: null,
				date: null,
				expirationDate: null,
				participants: []
			});

			this.reset();
		};
	}

	function pageHandler() {
		makeCall("GET", "MeetingHandler", null, function(req) {
			if (req.readyState == 4) {
				meetingData = new MeetingData();
				if (req.status == 200) {
					meetingData.loadContent(JSON.parse(req.responseText));

				} else {
					meetingData.logout();
				}

			}
		})
	}
})();
