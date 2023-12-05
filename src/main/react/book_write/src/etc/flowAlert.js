

// Alert 끄기
function dismissAlert(flowAlertId) {
  const alertElement = document.getElementById(`flow-alert-${flowAlertId}`);
  if (alertElement) {
    alertElement.classList.add('animate__fadeOutDown');

    setTimeout(() => {
      alertElement.remove();
    }, 500);
  }
}




let alertIdCounter = 1;

// eslint-disable-next-line no-unused-vars
export default function flowAlert(alertType, msg) {

  let color, bgColor, borderColor;
  const currentAlertId = alertIdCounter;

  switch(alertType) {
      case 'error': // red
          color = 'text-red-800';
          bgColor = 'bg-red-50';
          borderColor = 'border-red-300';
          break;
      case 'success': // green
          color = 'text-green-800';
          bgColor = 'bg-green-50';
          borderColor = 'border-green-300';
          break;
      case 'warning': // yellow
          color = 'text-yellow-800';
          bgColor = 'bg-yellow-50';
          borderColor = 'border-yellow-300';
          break;
      case 'neutral': // gray
          color = 'text-gray-800';
          bgColor = 'bg-gray-50';
          borderColor = 'border-gray-300';
          break;
      default: // info or blue
          color = 'text-blue-800';
          bgColor = 'bg-blue-50';
          borderColor = 'border-blue-300';
          break;
  }

  const alertHtml = `
      <div id="flow-alert-${currentAlertId}" class="items-center animate__animated animate__fadeInUp animate__faster opacity-90 p-4 mb-4 ${color} border-t-4 ${borderColor} ${bgColor}">
          <div class="flex justify-between">
              <svg class="flex-shrink-0 ml-2 w-4 h-4" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M10 .5a9.5 9.5 0 1 0 9.5 9.5A9.51 9.51 0 0 0 10 .5ZM9.5 4a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3ZM12 15H8a1 1 0 0 1 0-2h1v-3H8a1 1 0 0 1 0-2h2a1 1 0 0 1 1 1v4h1a1 1 0 0 1 0 2Z"/>
              </svg>
              <div>
                  <span id="flow-alert-${currentAlertId}-countdown" class="font-semibold text-xs">5</span>
                  <span class="font-semibold text-2xs">초후 제거</span>
                  <button type="button" class="ml-auto -mx-1.5 -my-1.5 ${bgColor} ${color} rounded-lg focus:ring-2 focus:ring-blue-400 p-1.5 hover:bg-blue-200 inline-flex items-center justify-center h-8 w-8" onclick="dismissAlert(${currentAlertId})">
                      <svg class="w-3 h-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
                          <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6"/>
                      </svg>
                  </button>
              </div>
          </div>
          <div class="ml-3 text-sm font-medium">
              ${msg}
          </div>
      </div>
  `;

  setTimeout(() => {
      dismissAlert(currentAlertId);
  }, 5000);

  const countDown = setInterval(() => {
      const countdown = document.getElementById(`flow-alert-${currentAlertId}-countdown`);
      const currentCount = parseInt(countdown.innerText);
      countdown.innerHTML = currentCount - 1;
  }, 1000);

  setTimeout(() => {
      clearInterval(countDown);
  }, 5000);

  alertIdCounter++;
  const alertContainer = document.getElementById('alert-container');
  if (alertContainer) {
    alertContainer.insertAdjacentHTML('beforeend', alertHtml);
  }
}
