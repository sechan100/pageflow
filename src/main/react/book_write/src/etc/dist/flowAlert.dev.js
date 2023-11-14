"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = flowAlert;

// Alert 끄기
function dismissAlert(flowAlertId) {
  var alertElement = document.getElementById("flow-alert-".concat(flowAlertId));

  if (alertElement) {
    alertElement.classList.add('animate__fadeOutDown');
    setTimeout(function () {
      alertElement.remove();
    }, 500);
  }
}

var alertIdCounter = 1;

function flowAlert(alertType, msg) {
  var color, bgColor, borderColor;
  var currentAlertId = alertIdCounter++;

  switch (alertType) {
    case 'error':
      // red
      color = 'text-red-800';
      bgColor = 'bg-red-50';
      borderColor = 'border-red-300';
      break;

    case 'success':
      // green
      color = 'text-green-800';
      bgColor = 'bg-green-50';
      borderColor = 'border-green-300';
      break;

    case 'warning':
      // yellow
      color = 'text-yellow-800';
      bgColor = 'bg-yellow-50';
      borderColor = 'border-yellow-300';
      break;

    case 'neutral':
      // gray
      color = 'text-gray-800';
      bgColor = 'bg-gray-50';
      borderColor = 'border-gray-300';
      break;

    default:
      // info or blue
      color = 'text-blue-800';
      bgColor = 'bg-blue-50';
      borderColor = 'border-blue-300';
      break;
  }

  var alertHtml = "\n      <div id=\"flow-alert-".concat(currentAlertId, "\" class=\"items-center animate__animated animate__fadeInUp animate__faster opacity-90 p-4 mb-4 ").concat(color, " border-t-4 ").concat(borderColor, " ").concat(bgColor, "\">\n          <div class=\"flex justify-between\">\n              <svg class=\"flex-shrink-0 ml-2 w-4 h-4\" xmlns=\"http://www.w3.org/2000/svg\" fill=\"currentColor\" viewBox=\"0 0 20 20\">\n                  <path d=\"M10 .5a9.5 9.5 0 1 0 9.5 9.5A9.51 9.51 0 0 0 10 .5ZM9.5 4a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3ZM12 15H8a1 1 0 0 1 0-2h1v-3H8a1 1 0 0 1 0-2h2a1 1 0 0 1 1 1v4h1a1 1 0 0 1 0 2Z\"/>\n              </svg>\n              <button type=\"button\" class=\"ml-auto -mx-1.5 -my-1.5 ").concat(bgColor, " ").concat(color, " rounded-lg focus:ring-2 focus:ring-blue-400 p-1.5 hover:bg-blue-200 inline-flex items-center justify-center h-8 w-8\" onclick=\"dismissAlert(").concat(currentAlertId, ")\">\n                  <svg class=\"w-3 h-3\" xmlns=\"http://www.w3.org/2000/svg\" fill=\"none\" viewBox=\"0 0 14 14\">\n                      <path stroke=\"currentColor\" stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"2\" d=\"m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6\"/>\n                  </svg>\n              </button>\n          </div>\n          <div class=\"ml-3 text-sm font-medium\">\n              ").concat(msg, "\n          </div>\n      </div>\n  ");
  setTimeout(function () {
    dismissAlert(currentAlertId);
  }, 10000);
  var alertContainer = document.getElementById('alert-container');

  if (alertContainer) {
    alertContainer.insertAdjacentHTML('beforeend', alertHtml);
  }
}