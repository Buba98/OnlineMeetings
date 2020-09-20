(function() {

	document.getElementById("loginbutton").addEventListener('click', (e) => {
		var form = e.target.closest("form");
		if (form.checkValidity()) {
			makeCall("POST", 'CheckLogin', e.target.closest("form"),
				function(req) {
					if (req.readyState == XMLHttpRequest.DONE) {
						var message = req.responseText;
						switch (req.status) {
							case 200:
								window.location.href = "homePage.html";
								break;
							case 400: // bad request
								document.getElementById("errormessage").textContent = message;
								break;
							case 401: // unauthorized
								document.getElementById("errormessage").textContent = message;
								break;
							case 500: // server error
								document.getElementById("errormessage").textContent = message;
								break;
						}
					}
				}
			);
		} else {
			form.reportValidity();
		}
	});

	document.getElementById("signupbutton").addEventListener('click', () => {
		document.getElementById("loginbutton").setAttribute("disabled", "disabled");
		document.getElementById("email").removeAttribute("hidden");
		document.getElementById("emailInput").setAttribute("placeholder", "example@polimi.it");
		document.getElementById("emailInput").removeAttribute("disabled");
		document.getElementById("signupbutton").addEventListener('click', (e) => {
			var form = e.target.closest("form");
			if (form.checkValidity()) {
				makeCall("POST", 'SignUp', e.target.closest("form"),
					function(req) {
						if (req.readyState == XMLHttpRequest.DONE) {
							var message = req.responseText;
							switch (req.status) {
								case 200:
									window.location.href = "homePage.html";
									break;
								case 400:
									document.getElementById("errormessage").textContent = message;
									break;
								case 406:
									document.getElementById("errormessage").textContent = message;
									break;
								case 500:
									document.getElementById("errormessage").textContent = message;
									break;
							}
						}
					}
				);
			} else {
				form.reportValidity();
			}
		});
	});
})();