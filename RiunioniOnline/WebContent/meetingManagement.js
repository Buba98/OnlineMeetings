(function() {
	var meetingData = null;

	window.addEventListener("load", () => {
		if (getCookie("person_id") == "" || getCookie("person_id") == null) {
			window.location.href = "index.html";
		} else {
			pageHandler();
		}
	}, false);



	function MeetingData() {
		var row, name, date, expirationDate, input, lable;
		var self = this;
		document.querySelector("a[href='Logout']").addEventListener('click', () => {
			self.logout();
		});

		this.newMeeting = ({
			name: null,
			date: null,
			expirationDate: null,
			partecipants: []
		});

		this.data = ({
			alert: document.getElementById("id_alert"),
			ownMeetings: document.getElementById("id_ownMeetingsTable"),
			otherMeetings: document.getElementById("id_otherMeetingsTable"),
			username: document.getElementById("id_username"),
			partecipantsModal: document.getElementById("id_newMeetingPartecipantsModal"),
			partecipantsInput: document.getElementById("id_newMeetingPartecipantsCheckBoxs"),
			formNewMeeting: document.getElementById("id_newMeeting"),
			buttonSelectPartecipants: document.getElementById("id_goToPartecipantsModal"),
			buttonSendNewMeeting: document.getElementById("id_sendNewMeeting")
		});
		this.loadContent = function(json) {

			this.data.buttonSelectPartecipants.addEventListener('click', (e) => {

				var form = e.target.closest("form");

				if (form.checkValidity()) {

					this.newMeeting.name = form.name.value;
					this.newMeeting.date = form.date.value + " " + form.hourAndMinutes.value + ":00";
					this.newMeeting.expirationDate = form.expirationDate.value + " " + form.expirationHourAndMinutes.value + ":00";

					this.data.partecipantsModal.style.display = "block";
				} else {
					form.reportValidity();
				}
			}, false);

			self.data.buttonSendNewMeeting.addEventListener('click', () => {

				document.getElementById("id_alert_newMeeting").innerText = "";

				var chk_arr = document.getElementsById("checkbox[]");

				for (k = 0; k < chk_arr.length; k++) {
					if (chk_arr[k].checked) {
						self.newMeeting.partecipants.push(chk_arr[k].value);
					}
				}

				if (self.newMeeting.partecipants.length > 0) {

					makeCall("POST", 'MeetingHandler', JSON.stringify(this.newMeeting),
						function(req) {
							if (req.readyState == XMLHttpRequest.DONE) {
								var message = req.responseText;
								switch (req.status) {
									case 200:
										this.updateNewMeeting();
										break;
									case 401:
										this.logout();
										break;
								}
								this.alert(message);
							}
						}
					);
				} else {
					document.getElementById("id_alert_newMeeting").innerText = "Select at least 1 participant"
				}
			});



			window.addEventListener('click', (e) => {
				if (e.target == this.data.partecipantsModal) {
					this.data.partecipantsModal.style.display = "none";
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
				expirationDate.textContent = meeting.expirationDate;

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
				expirationDate.textContent = meeting.expirationDate;

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
					input.setAttribute("id", "checkbox[]")

					lable = document.createElement("lable");
					lable.innerText = idAndName.username;

					self.data.partecipantsInput.appendChild(input);
					self.data.partecipantsInput.appendChild(lable);

					self.data.partecipantsInput.appendChild(document.createElement("br"));
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

		this.updateNewMeeting = function() {
			row = document.createElement("tr");

			name = document.createElement("td");
			name.textContent = this.newMeeting.name;

			date = document.createElement("td");
			date.textContent = this.newMeeting.date;

			expirationDate = document.createElement("td");
			expirationDate.textContent = meeting.expirationDate;

			row.appendChild(name);
			row.appendChild(date);
			row.appendChild(expirationDate);

			this.data.ownMeetings.appendChild(row);

			this.newMeeting = ({
				name: null,
				date: null,
				expirationDate: null,
				partecipants: []
			});
		};
	}

	function pageHandler() {
		makeCall("GET", "MeetingHandler", null, function(req) {
			if (req.readyState == 4) {
				console.log(req);
				var message = req.responseText;
				meetingData = new MeetingData();
				if (req.status == 200) {
					console.log(JSON.parse(req.responseText));
					meetingData.loadContent(JSON.parse(req.responseText));
					meetingData.alert(message);

				} else {
					meetingData.logout();
				}

			}
		})
	}
})();
