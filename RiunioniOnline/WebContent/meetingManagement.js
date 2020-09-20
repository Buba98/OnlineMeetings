(function() {

	this.newMeeting = ({
		name: null,
		date: null,
		expirationDate: null,
		partecipants: null
	});

	var meetingData = null;

	window.addEventListener("load", () => {
		if (getCookie("person_id") == "" || getCookie("person_id") == null) {
			window.location.href = "index.html";
		} else {
			pageHandler();
		}
	}, false);



	function MeetingData() {
		var row, name, expirationDate, input;
		var self = this;
		document.querySelector("a[href='Logout']").addEventListener('click', () => {
			self.logout;
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
				console.log(form.name);

				if (form.checkValidity()) {
					this.newMeeting.name = form.name;
					this.newMeeting.date = form.date + form.hourAndMinutes;
					this.newMeeting.expirationDate = form.expirationDate + form.expirationHourAndMinutes;
					this.data.partecipantsModal.style.display = "block";
				} else {
					form.reportValidity();
				}
			}, false);

			this.data.buttonSendNewMeeting.addEventListener('click', () => {

			});



			window.addEventListener('click', (e) => {
				if (e.target == this.data.partecipantsModal) {
					this.data.partecipantsModal.partecipantsModal.style.display = "none";
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

				this.data.ownMeetings.appendChild(row);
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

				this.data.otherMeetings.appendChild(row);
			});

			json.idsAndNames.forEach(function(idAndName) {
				if (idAndName.id != getCookie("person_id")) {
					input = document.createElement("input");
					input.setAttribute("type", "checkbox");
					input.setAttribute("value", idAndName.name);
					input.textContent = idAndName.userName;

					this.data.partecipantsInput.appendChild(input);
					this.data.partecipantsInput.appendChild(document.createElement("br"));
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
