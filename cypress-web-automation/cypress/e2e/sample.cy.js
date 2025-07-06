describe('Cypress documentation site', () => {
  it('loads and shows the dashboard link', () => {
    cy.visit('https://docs.cypress.io');
    cy.contains('Dashboard');
  });
});
