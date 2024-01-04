/////////////////////////////////////////////////
// Responsive VIDEO.JS
// Include this file only where using video.js
/////////////////////////////////////////////////

// on document.ready
$(function() {
	
	// Once the video is ready (http://daverupert.com/2012/05/making-video-js-fluid-for-rwd)
	document.getElementById("video-id") &&
	_V_("video-id").ready(function(){

	  var myPlayer = this;    // Store the video object
	  var aspectRatio = 9/16; // Make up an aspect ratio

	  function resizeVideoJS(){
		// Get the parent element's actual width
		var width = document.getElementById(myPlayer.L).parentElement.offsetWidth;
		// Set width to fill parent element, Set height
		myPlayer.width(width).height( width * aspectRatio );
	  }

	  resizeVideoJS(); // Initialize the function
	  window.onresize = resizeVideoJS; // Call the function on resize

	});

})