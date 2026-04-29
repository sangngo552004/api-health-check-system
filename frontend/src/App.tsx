export function App() {
  return (
    <main className="app-shell">
      <section className="hero">
        <p className="eyebrow">API Health Check System</p>
        <h1>Dashboard scaffold for monitoring services and response health.</h1>
        <p className="lead">
          Giai doan 2 dung khung frontend. Cac widget, bieu do va du lieu that
          se duoc bo sung o cac giai doan tiep theo.
        </p>
      </section>

      <section className="grid">
        <article className="card">
          <span className="card-label">Endpoints</span>
          <strong>0</strong>
          <p>Danh sach endpoint se duoc ket noi tu backend.</p>
        </article>

        <article className="card">
          <span className="card-label">Open Incidents</span>
          <strong>0</strong>
          <p>Khoi cho dashboard duoc tao san de noi API sau.</p>
        </article>

        <article className="card wide">
          <span className="card-label">Status Overview</span>
          <p>
            Layout nay la baseline cho dashboard monitoring, giu nhe de chuyen
            sang phase 3 va phase 5 ma khong phai lam lai cau truc.
          </p>
        </article>
      </section>
    </main>
  );
}
