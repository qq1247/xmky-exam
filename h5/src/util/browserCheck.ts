
import bowser from 'bowser';

export function checkBrowserCompatibility() {
    const browser = bowser.getParser(window.navigator.userAgent);
    const minVersions = {
        chrome: 107,
        edge: 107,
        firefox: 104,
        safari: 16,
    };

    const result = browser.satisfies({
        chrome: `>=${minVersions.chrome}`,
        firefox: `>=${minVersions.firefox}`,
        safari: `>=${minVersions.safari}`,
        edge: `>=${minVersions.edge}`
    });

    return {
        isSupported: result,
        browserName: browser.getBrowserName(),
        currentVersion: browser.getBrowserVersion()
    };
}

export function getDownloadLinks() {
    return {
        chrome: '[Chrome下载](https://www.google.com/chrome/)',
        firefox: '[Firefox下载](https://www.mozilla.org/firefox/)',
        edge: '[Edge下载](https://www.microsoft.com/edge)',
        safari: '[Safari更新](https://support.apple.com/downloads/safari)'
    };
}
