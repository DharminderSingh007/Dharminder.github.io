const { Given, When, Then } = require('@cucumber/cucumber');
const { expect } = require('chai');
const { remote } = require('webdriverio');

let browser;

Given('I open Google\'s homepage', async () => {
  browser = await remote({ capabilities: { browserName: 'chrome' } });
  await browser.url('https://www.google.com');
});

When('I search for {string}', async (query) => {
  const input = await browser.$('input[name="q"]');
  await input.setValue(query);
  await browser.keys('Enter');
});

Then('the results page shows links related to WebDriverIO', async () => {
  const results = await browser.$$('a');
  expect(results.length).to.be.greaterThan(0);
  await browser.deleteSession();
});
