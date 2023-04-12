function scrollToBottom() {
  window.scrollTo({
    top: document.body.scrollHeight,
    behavior: 'smooth'
  });
}

window.CodeGPTBridge = {
  displayLandingView: function () {
    const wrapper = document.createElement('div');
    wrapper.setAttribute('id', 'landing-view');
    wrapper.appendChild(createElement({
      tagName: 'div', innerHTML: "<h1>Examples</h1>" +
          "<span class=\"example-text\">\"How do I make an HTTP request in Javascript?\"</span>" +
          "<span class=\"example-text\">\"What is the difference between px, dip, dp, and sp?\"</span>" +
          "<span class=\"example-text\">\"How do I undo the most recent local commits in Git?\"</span>" +
          "<span class=\"example-text\">\"What is the difference between stack and heap?\"</span>"
    }));
    document.body.appendChild(wrapper);
  },
  displayUserMessage: function (accountName, htmlMessage, deleteSvgIcon) {
    document.getElementById("landing-view")?.remove();

    const actions = createElement({tagName: 'span', className: 'user-message-actions'});
    actions.appendChild(createElement({tagName: 'button', className: 'delete-icon-wrapper', innerHTML: deleteSvgIcon}));

    const nameWrapper = createElement({tagName: 'p', className: 'user-message-name-wrapper'});
    nameWrapper.appendChild(createElement({tagName: 'span', textContent: accountName}));
    nameWrapper.appendChild(actions);

    const wrapper = createElement({tagName: 'div', className: 'user-message'});
    wrapper.appendChild(nameWrapper);
    wrapper.appendChild(createElement({tagName: 'div', innerHTML: htmlMessage}));
    document.body.appendChild(wrapper);

    scrollToBottom();
    Prism.highlightAll();
  },
  displayErrorMessage: function (responseId, errorMsgHtml) {
    document.getElementById(responseId).appendChild(createElement({tagName: 'p', innerHTML: errorMsgHtml}));
  },
  displayMissingCredential: function (responseId) {
    const responseWrapper = document.getElementById(responseId);
    // TODO: Add anchor link for opening the settings panel
    responseWrapper.innerHTML = "<p>API key not provided.</p>";
  },
  displayResponse: function (responseId, animate, svgIcon) {
    const wrapper = createElement({tagName: 'div', className: 'response'})

    const iconLabelContainer = document.createElement('p');

    const svgWrapper = createElement({tagName: 'span', className: 'icon-wrapper', innerHTML: svgIcon});
    if (animate) {
      svgWrapper.style.animation = 'roll 2.4s infinite linear';
    }
    iconLabelContainer.appendChild(svgWrapper);
    const label = document.createElement('strong');
    label.textContent = 'CodeGPT';
    iconLabelContainer.appendChild(label);
    wrapper.appendChild(iconLabelContainer);

    const responseWrapper = createElement({tagName: 'div', innerHTML: "<p>&#8205;</p>"})
    responseWrapper.setAttribute('id', responseId);
    wrapper.appendChild(responseWrapper);
    document.body.appendChild(wrapper);
    scrollToBottom();
  },
  clearResponse: function (responseId) {
    const responseWrapper = document.getElementById(responseId);
    responseWrapper.innerHTML = "<p>&#8205;</p>"
  },
  replaceResponseContent: function (responseId, htmlContent) {
    const responseWrapper = document.getElementById(responseId);
    responseWrapper.innerHTML = htmlContent;
    scrollToBottom();
    Prism.highlightAll();
  },
  updateRegenerateButton: function (title, isDisabled) {
    const buttons = document.getElementsByClassName('replace-button');
    for (let i = 0; i < buttons.length; i++) {
      buttons[i].disabled = isDisabled;
      buttons[i].title = title;
    }
  },
  animateCodeGPTSvg: function (responseId) {
    const response = document.getElementById(responseId);
    const svg = response.parentElement.getElementsByClassName('icon-wrapper')[0];
    svg.style.animation = 'roll 2.4s infinite linear';
  },
  stopTyping: function () {
    const icons = Array.from(document.getElementsByClassName('icon-wrapper'));
    for (const icon of icons) {
      icon.style = null;
    }
  }
}

const createElement = (props) => {
  const element = document.createElement(props.tagName);
  if (props.className) {
    element.classList.add(props.className);
  }
  if (props.innerHTML) {
    element.innerHTML = props.innerHTML;
  }
  if (props.textContent) {
    element.textContent = props.textContent;
  }
  return element;
}
