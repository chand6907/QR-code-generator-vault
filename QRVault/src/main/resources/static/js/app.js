/* QRVault — app.js */

let activeType    = 'URL';
let lastBase64    = null;

// ── Type tab switching ─────────────────────────────────────────────────────

function switchType(type, btn) {
  document.querySelectorAll('.type-tab').forEach(t => t.classList.remove('active'));
  document.querySelectorAll('.type-panel').forEach(p => p.classList.remove('active'));
  btn.classList.add('active');
  document.getElementById('panel-' + type).classList.add('active');
  activeType = type;
}

// ── Color label sync ──────────────────────────────────────────────────────

document.getElementById('fgColor').addEventListener('input', function () {
  document.getElementById('fgHex').textContent = this.value;
});

document.getElementById('bgColor').addEventListener('input', function () {
  document.getElementById('bgHex').textContent = this.value;
});

// ── Generate QR ───────────────────────────────────────────────────────────

async function generateQR() {
  const btn    = document.getElementById('generateBtn');
  const btnTxt = document.getElementById('btnText');
  const loader = document.getElementById('btnLoader');

  // Show loader
  btnTxt.textContent = 'Generating…';
  loader.style.display = 'inline-block';
  btn.disabled = true;

  // Collect CSRF token
  const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');

  // Build form data
  const params = new URLSearchParams();
  params.append('qrType',   activeType);
  params.append('size',     document.getElementById('qrSize').value);
  params.append('ecLevel',  document.getElementById('ecLevel').value);
  params.append('fgColor',  document.getElementById('fgColor').value);
  params.append('bgColor',  document.getElementById('bgColor').value);

  // Type-specific fields
  if (activeType === 'URL') {
    params.append('urlData', document.getElementById('urlData').value.trim() || 'https://example.com');
  } else if (activeType === 'TEXT') {
    params.append('textData', document.getElementById('textData').value.trim() || 'Hello World');
  } else if (activeType === 'WIFI') {
    params.append('wifiSsid',     document.getElementById('wifiSsid').value.trim());
    params.append('wifiPassword', document.getElementById('wifiPassword').value);
    params.append('wifiSecurity', document.getElementById('wifiSecurity').value);
  } else if (activeType === 'CONTACT') {
    params.append('contactFirst', document.getElementById('contactFirst').value.trim());
    params.append('contactLast',  document.getElementById('contactLast').value.trim());
    params.append('contactPhone', document.getElementById('contactPhone').value.trim());
    params.append('contactEmail', document.getElementById('contactEmail').value.trim());
  }

  try {
    const res  = await fetch('/generate', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        'X-CSRF-TOKEN': csrfToken
      },
      body: params.toString()
    });

    const json = await res.json();

    if (json.success) {
      lastBase64 = json.image;
      showQR(json.image);
    } else {
      alert('Error: ' + (json.error || 'Something went wrong.'));
    }
  } catch (err) {
    alert('Network error. Please try again.');
    console.error(err);
  } finally {
    btnTxt.textContent = 'Generate QR Code';
    loader.style.display = 'none';
    btn.disabled = false;
  }
}

// ── Display QR result ──────────────────────────────────────────────────────

function showQR(src) {
  const img    = document.getElementById('qrImage');
  const empty  = document.getElementById('outputEmpty');
  const result = document.getElementById('outputResult');
  const meta   = document.getElementById('outputMeta');

  img.src = src;
  empty.style.display  = 'none';
  result.style.display = 'flex';

  const size = document.getElementById('qrSize').value;
  const ec   = document.getElementById('ecLevel').value;
  const ecLabels = { L: 'Low', M: 'Medium', Q: 'High', H: 'Max' };
  meta.textContent = `${activeType} · ${size}×${size} px · Error correction: ${ecLabels[ec]}`;

  // Smooth scroll to output on mobile
  if (window.innerWidth < 900) {
    result.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
  }
}

// ── Download ───────────────────────────────────────────────────────────────

function downloadQR() {
  if (!lastBase64) return;
  const form = document.getElementById('downloadForm');
  document.getElementById('downloadData').value = lastBase64;
  form.submit();
}

// ── Copy image to clipboard ────────────────────────────────────────────────

async function copyQR() {
  if (!lastBase64) return;
  try {
    const res  = await fetch(lastBase64);
    const blob = await res.blob();
    await navigator.clipboard.write([new ClipboardItem({ 'image/png': blob })]);

    const btn = document.querySelector('.btn-copy');
    btn.textContent = '✓ Copied!';
    setTimeout(() => btn.textContent = '⎘ Copy image', 2000);
  } catch {
    alert('Could not copy to clipboard. Please download the image instead.');
  }
}

// ── Allow Enter key to generate ───────────────────────────────────────────

document.addEventListener('keydown', function (e) {
  if (e.key === 'Enter' && (e.ctrlKey || e.metaKey)) {
    generateQR();
  }
});
