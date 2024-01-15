document.addEventListener('DOMContentLoaded', () => {
  const accordionButton = document.querySelector('.accordion-button');

  accordionButton.addEventListener('click', (e) => {
    e.preventDefault();

    const collapseOne = new bootstrap.Collapse(
        document.getElementById('collapseOne'));
    collapseOne.toggle();
  });
});