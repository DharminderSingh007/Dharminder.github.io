const { remote } = require('webdriverio');

(async () => {
  const driver = await remote({
    path: '/wd/hub',
    port: 4723,
    capabilities: {
      platformName: 'Android',
      appium: { options: { app: '/path/to/app.apk' } },
      browserName: 'Chrome'
    }
  });

  await driver.url('https://appium.io');
  const title = await driver.getTitle();
  console.log('Title:', title);

  await driver.deleteSession();
})();
