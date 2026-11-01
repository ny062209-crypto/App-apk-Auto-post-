import React, { useState, useEffect } from 'react';
import {
  Shield,
  User,
  Users,
  Send,
  StopCircle,
  Plus,
  Home,
  BarChart3,
  Settings as SettingsIcon,
  CheckCircle2,
  XCircle,
  Clock,
  Trash2,
  Check,
  ChevronRight,
  ExternalLink,
  Code2,
  Download,
  AlertTriangle,
  Sparkles,
  Smartphone
} from 'lucide-react';

interface TelegramAccount {
  id: number;
  name: string;
  username: string;
  phone: string;
  active: boolean;
}

interface TelegramGroup {
  id: number;
  title: string;
  members: number;
  selected: boolean;
}

interface PostReport {
  id: string;
  time: string;
  group: string;
  member: string;
  username: string;
  status: 'SUCCESS' | 'FAILED' | 'FLOOD_WAIT';
  floodSeconds?: number;
  message: string;
}

export default function App() {
  const [activeTab, setActiveTab] = useState<'home' | 'accounts' | 'groups' | 'post' | 'reports' | 'settings'>('home');
  const [isPostingActive, setIsPostingActive] = useState(false);
  const [safeDelay, setSafeDelay] = useState(40);
  const [mentionUsername, setMentionUsername] = useState(true);
  const [messageText, setMessageText] = useState('សួស្តី! សូមស្វាគមន៍មកកាន់ក្រុមរបស់យើង។ សូមចុចចូលរួមឆានែលខាងក្រោមដើម្បីទទួលបានព័ត៌មានថ្មីៗ និងប្រូម៉ូសិនពិសេសៗ!');
  const [linksText, setLinksText] = useState("https://t.me/kiragaming\nhttps://t.me/kiraofficial");
  const [mediaAttachment, setMediaAttachment] = useState<string | null>('banner_welcome.jpg');
  const [showCodeModal, setShowCodeModal] = useState(false);

  // Accounts state
  const [accounts, setAccounts] = useState<TelegramAccount[]>([
    {
      id: 1,
      name: 'KIRA Gaming',
      username: 'kiragaming',
      phone: '+855 96 **** 123',
      active: true,
    },
  ]);

  // Groups state
  const [groups, setGroups] = useState<TelegramGroup[]>([
    { id: 1, title: 'KIRA Gaming Community KH', members: 14200, selected: true },
    { id: 2, title: 'Telegram Auto Post & Tools KH', members: 8540, selected: true },
    { id: 3, title: 'Crypto & Airdrop Cambodia', members: 22100, selected: false },
    { id: 4, title: 'E-Commerce & Online Shop KH', members: 6300, selected: false },
  ]);

  // Reports state
  const [reports, setReports] = useState<PostReport[]>([
    {
      id: 'log-1',
      time: '18:42:15',
      group: 'KIRA Gaming Community KH',
      member: 'Sokha Mean',
      username: 'sokha_kh',
      status: 'SUCCESS',
      message: 'សួស្តី @sokha_kh សូមស្វាគមន៍មកកាន់ក្រុម KIRA Gaming!',
    },
  ]);

  const [stats, setStats] = useState({
    success: 1,
    failed: 0,
    flood: 0,
  });

  // Safe sending live timer simulation
  const [nextSendCountdown, setNextSendCountdown] = useState<number | null>(null);

  useEffect(() => {
    let interval: NodeJS.Timeout;
    if (isPostingActive) {
      setNextSendCountdown(safeDelay);
      interval = setInterval(() => {
        setNextSendCountdown((prev) => {
          if (prev === null || prev <= 1) {
            // Trigger simulated join
            simulateNewJoin();
            return safeDelay;
          }
          return prev - 1;
        });
      }, 1000);
    } else {
      setNextSendCountdown(null);
    }
    return () => clearInterval(interval);
  }, [isPostingActive, safeDelay]);

  const simulateNewJoin = () => {
    const selectedGroups = groups.filter((g) => g.selected);
    if (selectedGroups.length === 0) return;

    const randomGroup = selectedGroups[Math.floor(Math.random() * selectedGroups.length)];
    const sampleNames = ['Rathana Kim', 'Vireak Dara', 'Chhaya Long', 'Bopha Pich', 'Narith Seng'];
    const sampleUsernames = ['rathana_pro', 'vireak99', 'chhaya_kh', 'bopha_p', 'narith_gaming'];
    const idx = Math.floor(Math.random() * sampleNames.length);

    const now = new Date();
    const timeStr = now.toTimeString().split(' ')[0];

    const isFlood = Math.random() < 0.05;
    const isFail = Math.random() < 0.03;

    if (isFlood) {
      const wait = 60;
      const newReport: PostReport = {
        id: `log-${Date.now()}`,
        time: timeStr,
        group: randomGroup.title,
        member: sampleNames[idx],
        username: sampleUsernames[idx],
        status: 'FLOOD_WAIT',
        floodSeconds: wait,
        message: `Flood wait required: ${wait} seconds from Telegram`,
      };
      setReports((prev) => [newReport, ...prev]);
      setStats((prev) => ({ ...prev, flood: prev.flood + 1 }));
    } else if (isFail) {
      const newReport: PostReport = {
        id: `log-${Date.now()}`,
        time: timeStr,
        group: randomGroup.title,
        member: sampleNames[idx],
        username: sampleUsernames[idx],
        status: 'FAILED',
        message: 'Send failed: Chat write restricted',
      };
      setReports((prev) => [newReport, ...prev]);
      setStats((prev) => ({ ...prev, failed: prev.failed + 1 }));
    } else {
      const mention = mentionUsername ? `@${sampleUsernames[idx]} ` : '';
      const newReport: PostReport = {
        id: `log-${Date.now()}`,
        time: timeStr,
        group: randomGroup.title,
        member: sampleNames[idx],
        username: sampleUsernames[idx],
        status: 'SUCCESS',
        message: `${mention}${messageText.slice(0, 50)}...`,
      };
      setReports((prev) => [newReport, ...prev]);
      setStats((prev) => ({ ...prev, success: prev.success + 1 }));
    }
  };

  const toggleGroup = (id: number) => {
    setGroups(groups.map((g) => (g.id === id ? { ...g, selected: !g.selected } : g)));
  };

  return (
    <div className="min-h-screen bg-slate-100 flex flex-col items-center justify-center p-2 sm:p-4 select-none font-sans text-slate-800">
      {/* Top Banner Notice for GitHub & APK */}
      <div className="w-full max-w-[440px] mb-2 bg-gradient-to-r from-blue-700 to-indigo-700 text-white p-3 rounded-2xl shadow flex items-center justify-between text-xs sm:text-sm">
        <div className="flex items-center gap-2">
          <Smartphone className="w-4 h-4 text-blue-200 shrink-0" />
          <span>Android Source Code & Workflow រួចរាល់ 100%</span>
        </div>
        <div className="flex items-center gap-1.5">
          <a
            href="/kira-auto-post-android.zip"
            download="kira-auto-post-android.zip"
            className="bg-emerald-500 hover:bg-emerald-600 text-white font-semibold px-2.5 py-1 rounded-lg text-xs flex items-center gap-1 transition shadow-xs"
          >
            <Download className="w-3.5 h-3.5" /> Download ZIP
          </a>
          <button
            onClick={() => setShowCodeModal(true)}
            className="bg-white/20 hover:bg-white/30 text-white font-semibold px-2.5 py-1 rounded-lg text-xs flex items-center gap-1 transition"
          >
            <Code2 className="w-3.5 h-3.5" /> ការណែនាំ
          </button>
        </div>
      </div>

      {/* Main Mobile App Frame replicating Image 2 */}
      <div className="w-full max-w-[420px] bg-white rounded-3xl shadow-2xl border border-slate-200 overflow-hidden flex flex-col h-[840px] relative">
        {/* Android Status Bar Mock */}
        <div className="px-6 pt-2 pb-1 flex items-center justify-between text-xs text-slate-600 font-medium">
          <span>8:52</span>
          <div className="flex items-center gap-1.5">
            <span className="text-[10px] bg-slate-200 px-1 rounded">4G</span>
            <span>20%</span>
          </div>
        </div>

        {/* Header matching Image 2 */}
        <div className="px-4 py-3 flex items-center justify-between border-b border-slate-100">
          <div className="flex items-center gap-3">
            {/* Logo box */}
            <div className="w-11 h-11 rounded-xl bg-black border border-slate-200 flex items-center justify-center overflow-hidden shadow-sm shrink-0">
              <img
                src="/public/assets/aistudio/ff095ad2d446b14376840a99b442d08f_2e9c3a1b.jpg"
                alt="KIRA Logo"
                className="w-10 h-10 object-contain"
              />
            </div>
            <div>
              <h1 className="font-extrabold text-lg text-slate-900 leading-tight">KIRA Auto Post</h1>
              <p className="text-xs text-slate-500 font-medium">បង្កើតឡើងដោយ KIRA Gaming</p>
            </div>
          </div>
          <button
            onClick={() => setActiveTab('settings')}
            className="w-10 h-10 flex items-center justify-center rounded-full hover:bg-blue-50 text-blue-600 transition"
          >
            <SettingsIcon className="w-6 h-6 fill-current text-blue-600" />
          </button>
        </div>

        {/* Main Content Area */}
        <div className="flex-1 overflow-y-auto p-4 space-y-4">
          {activeTab === 'home' && (
            <>
              {/* 1. Safe Sending Card (Exact like Image 2) */}
              <div className="bg-[#eff6ff] border border-[#bfdbfe] rounded-2xl p-4 flex items-center gap-3.5 shadow-sm">
                <div className="w-14 h-14 rounded-full bg-[#1d68ec] flex items-center justify-center shrink-0 shadow-inner">
                  <Shield className="w-7 h-7 text-white fill-current" />
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-center justify-between">
                    <h2 className="font-bold text-[14px] text-slate-900 leading-snug">
                      ការការពារគណនី (Safe Sending {safeDelay}s)
                    </h2>
                    {isPostingActive && (
                      <span className="text-[10px] bg-green-500 text-white font-bold px-1.5 py-0.5 rounded-full animate-pulse">
                        LIVE
                      </span>
                    )}
                  </div>
                  <p className="text-[12px] text-slate-600 leading-relaxed mt-0.5">
                    គម្លាតពេលផុស {safeDelay} វិនាទី ដើម្បីការពារការ Ban ឬ Flood Wait ពី Telegram
                  </p>
                  {isPostingActive && nextSendCountdown !== null && (
                    <div className="mt-1.5 flex items-center gap-1.5 text-xs font-semibold text-blue-700">
                      <Clock className="w-3.5 h-3.5 animate-spin" />
                      <span>ផ្ញើសារបន្ទាប់ក្នុង: {nextSendCountdown} វិនាទី</span>
                    </div>
                  )}
                </div>
              </div>

              {/* 2. Summary Section */}
              <div className="space-y-2.5">
                <h3 className="font-bold text-base text-slate-900">ស្ថានភាពសរុប (Summary)</h3>
                <div className="grid grid-cols-2 gap-3">
                  {/* Accounts Card */}
                  <div
                    onClick={() => setActiveTab('accounts')}
                    className="bg-white border border-slate-200 rounded-2xl p-4 cursor-pointer hover:border-blue-300 transition shadow-xs"
                  >
                    <div className="flex items-center gap-1.5 text-blue-600 mb-1">
                      <User className="w-4 h-4 fill-current" />
                      <span className="font-medium text-xs text-slate-700">គណនី</span>
                    </div>
                    <div className="text-2xl font-black text-slate-900">{accounts.filter((a) => a.active).length}</div>
                    <div className="text-xs text-slate-500 font-medium">បានភ្ជាប់</div>
                  </div>

                  {/* Groups Selected Card */}
                  <div
                    onClick={() => setActiveTab('groups')}
                    className="bg-white border border-slate-200 rounded-2xl p-4 cursor-pointer hover:border-blue-300 transition shadow-xs"
                  >
                    <div className="flex items-center gap-1.5 text-blue-600 mb-1">
                      <Users className="w-4 h-4 fill-current" />
                      <span className="font-medium text-xs text-slate-700">ក្រុមបានជ្រើស</span>
                    </div>
                    <div className="text-2xl font-black text-slate-900">{groups.filter((g) => g.selected).length}</div>
                    <div className="text-xs text-slate-500 font-medium">ត្រៀមផុស</div>
                  </div>
                </div>
              </div>

              {/* 3. Results Card */}
              <div className="bg-white border border-slate-200 rounded-2xl p-4 shadow-xs">
                <div className="flex items-center justify-between mb-3">
                  <h4 className="font-bold text-sm text-slate-900">លទ្ធផលនៃការផុសកន្លងមក</h4>
                  <button
                    onClick={() => setActiveTab('reports')}
                    className="text-xs font-bold text-slate-700 hover:text-blue-600 flex items-center"
                  >
                    មើលរបាយការណ៍ &gt;
                  </button>
                </div>
                <div className="grid grid-cols-3 text-center divide-x divide-slate-100">
                  <div>
                    <div className="text-xl font-black text-slate-900">{stats.success}</div>
                    <div className="text-xs text-slate-500 font-medium mt-0.5">ជោគជ័យ</div>
                  </div>
                  <div>
                    <div className="text-xl font-black text-slate-900">{stats.failed}</div>
                    <div className="text-xs text-slate-500 font-medium mt-0.5">បរាជ័យ</div>
                  </div>
                  <div>
                    <div className="text-xl font-black text-slate-900">
                      <span className="border border-slate-400 rounded-full px-2 py-0.5 text-base">
                        {stats.flood}
                      </span>
                    </div>
                    <div className="text-xs text-slate-500 font-medium mt-0.5">រំលង</div>
                  </div>
                </div>
              </div>

              {/* 4. Quick Actions */}
              <div className="space-y-3 pt-1">
                <h3 className="font-bold text-base text-slate-900">សកម្មភាពរហ័ស (Quick Actions)</h3>

                {/* ONE toggle button: Start / Stop */}
                <button
                  onClick={() => setIsPostingActive(!isPostingActive)}
                  className={`w-full py-3.5 px-4 rounded-xl text-white font-bold flex items-center justify-center gap-2 shadow-md transition-all active:scale-[0.98] ${
                    isPostingActive
                      ? 'bg-red-600 hover:bg-red-700 text-white'
                      : 'bg-[#1d68ec] hover:bg-blue-700 text-white'
                  }`}
                >
                  {isPostingActive ? (
                    <>
                      <StopCircle className="w-5 h-5 fill-current" />
                      <span>បញ្ឈប់ការផុសសារ (Stop Posting)</span>
                    </>
                  ) : (
                    <>
                      <Send className="w-5 h-5 fill-current -rotate-12" />
                      <span>ចាប់ផ្តើមផុសសារឥឡូវ (Start Posting)</span>
                    </>
                  )}
                </button>

                {/* Outlined Add Account Button */}
                <button
                  onClick={() => setActiveTab('accounts')}
                  className="w-full py-3 px-4 rounded-xl border-2 border-[#1d68ec] text-[#1d68ec] hover:bg-blue-50 font-bold flex items-center justify-center gap-1.5 transition active:scale-[0.98]"
                >
                  <Plus className="w-5 h-5 stroke-[3]" />
                  <span>បន្ថែមគណនី Telegram ថ្មី</span>
                </button>
              </div>

              {/* Quick simulation helper */}
              <div className="bg-slate-50 border border-dashed border-slate-300 rounded-xl p-3 text-center">
                <p className="text-xs text-slate-500 mb-2">
                  សាកល្បងតេស្ត Join ភ្លាមៗ (Test Auto-Reply simulation):
                </p>
                <button
                  onClick={simulateNewJoin}
                  className="px-3 py-1.5 bg-slate-200 hover:bg-slate-300 rounded-lg text-xs font-semibold text-slate-700 inline-flex items-center gap-1"
                >
                  <Sparkles className="w-3.5 h-3.5 text-blue-600" /> ក្លែងធ្វើមានសមាជិកថ្មីចូលក្រុម
                </button>
              </div>
            </>
          )}

          {/* TAB 2: ACCOUNTS */}
          {activeTab === 'accounts' && (
            <div className="space-y-4">
              <div className="border-b pb-2">
                <h3 className="font-bold text-base text-slate-900">គ្រប់គ្រងគណនី Telegram (TDLib User)</h3>
                <p className="text-xs text-slate-500">តភ្ជាប់គណនី Telegram ពិតប្រាកដតាមរយៈ API ID & Hash</p>
              </div>

              <div className="bg-blue-50 border border-blue-200 rounded-2xl p-4 space-y-3">
                <h4 className="font-bold text-sm text-slate-800">បន្ថែមគណនីថ្មី</h4>
                <div>
                  <label className="text-xs font-semibold text-slate-600">Telegram API ID</label>
                  <input
                    type="text"
                    defaultValue="29841234"
                    className="w-full mt-1 px-3 py-2 bg-white border border-slate-200 rounded-lg text-xs font-mono"
                  />
                </div>
                <div>
                  <label className="text-xs font-semibold text-slate-600">Telegram API Hash</label>
                  <input
                    type="text"
                    defaultValue="a1b2c3d4e5f60718293a4b5c6d7e8f90"
                    className="w-full mt-1 px-3 py-2 bg-white border border-slate-200 rounded-lg text-xs font-mono"
                  />
                </div>
                <div>
                  <label className="text-xs font-semibold text-slate-600">លេខទូរស័ព្ទ (Phone Number)</label>
                  <input
                    type="text"
                    placeholder="+855 12 345 678"
                    className="w-full mt-1 px-3 py-2 bg-white border border-slate-200 rounded-lg text-xs"
                  />
                </div>
                <button
                  onClick={() => alert('កូដផ្ទៀងផ្ទាត់ត្រូវបានផ្ញើទៅ Telegram App របស់អ្នក!')}
                  className="w-full py-2.5 bg-blue-600 text-white font-bold rounded-xl text-xs hover:bg-blue-700 transition"
                >
                  ផ្ញើកូដផ្ទៀងផ្ទាត់ (Send Login Code)
                </button>
              </div>

              <div>
                <h4 className="font-bold text-sm text-slate-800 mb-2">គណនីដែលបានភ្ជាប់ ({accounts.length})</h4>
                {accounts.map((acc) => (
                  <div key={acc.id} className="bg-white border rounded-xl p-3 flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 font-bold">
                        <User className="w-5 h-5" />
                      </div>
                      <div>
                        <div className="font-bold text-sm text-slate-900">{acc.name}</div>
                        <div className="text-xs text-slate-500">@{acc.username} • {acc.phone}</div>
                        <div className="text-[11px] text-green-600 font-medium">● សកម្ម (Active TDLib Session)</div>
                      </div>
                    </div>
                    <button
                      onClick={() => alert('គណនីត្រូវបានលុបចេញពីឧបករណ៍!')}
                      className="text-red-500 hover:text-red-700 p-2"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* TAB 3: GROUPS */}
          {activeTab === 'groups' && (
            <div className="space-y-3">
              <div className="flex items-center justify-between border-b pb-2">
                <div>
                  <h3 className="font-bold text-base text-slate-900">ជ្រើសរើសក្រុមស្វ័យប្រវត្តិតប</h3>
                  <p className="text-xs text-slate-500">
                    បានជ្រើស {groups.filter((g) => g.selected).length}/{groups.length} ក្រុម
                  </p>
                </div>
                <button
                  onClick={() => {
                    const allSelected = groups.every((g) => g.selected);
                    setGroups(groups.map((g) => ({ ...g, selected: !allSelected })));
                  }}
                  className="text-xs font-bold text-blue-600"
                >
                  ជ្រើសទាំងអស់
                </button>
              </div>

              <div className="space-y-2">
                {groups.map((grp) => (
                  <div
                    key={grp.id}
                    onClick={() => toggleGroup(grp.id)}
                    className={`p-3.5 rounded-xl border flex items-center justify-between cursor-pointer transition ${
                      grp.selected ? 'bg-blue-50/70 border-blue-200' : 'bg-white border-slate-200'
                    }`}
                  >
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-600">
                        <Users className="w-5 h-5" />
                      </div>
                      <div>
                        <div className="font-bold text-sm text-slate-900">{grp.title}</div>
                        <div className="text-xs text-slate-500">{grp.members.toLocaleString()} សមាជិក</div>
                      </div>
                    </div>
                    <div
                      className={`w-6 h-6 rounded-md flex items-center justify-center border transition ${
                        grp.selected ? 'bg-blue-600 border-blue-600 text-white' : 'border-slate-300'
                      }`}
                    >
                      {grp.selected && <Check className="w-4 h-4 stroke-[3]" />}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* TAB 4: POST EDITOR */}
          {activeTab === 'post' && (
            <div className="space-y-3">
              <div className="border-b pb-2">
                <h3 className="font-bold text-base text-slate-900">សារស្វ័យប្រវត្តិ (Auto-Reply Template)</h3>
                <p className="text-xs text-slate-500">រៀបចំសារដែលត្រូវតបទៅសមាជិកថ្មី</p>
              </div>

              {/* Mention checkbox */}
              <label className="flex items-start gap-2.5 bg-blue-50 border border-blue-100 p-3 rounded-xl cursor-pointer">
                <input
                  type="checkbox"
                  checked={mentionUsername}
                  onChange={(e) => setMentionUsername(e.target.checked)}
                  className="w-4 h-4 mt-0.5 rounded text-blue-600 focus:ring-blue-500"
                />
                <div>
                  <div className="text-xs font-bold text-slate-800">
                    Mention @username សមាជិកថ្មីនៅលើបន្ទាត់ដំបូង
                  </div>
                  <div className="text-[11px] text-slate-500">
                    ពេលមានសមាជិកថ្មីចូល នឹងដាក់ @username របស់គាត់នៅដើមសារស្វ័យប្រវត្តិ
                  </div>
                </div>
              </label>

              {/* Message text */}
              <div>
                <label className="text-xs font-bold text-slate-700">ខ្លឹមសារសារ (Message Text)</label>
                <textarea
                  value={messageText}
                  onChange={(e) => setMessageText(e.target.value)}
                  rows={4}
                  className="w-full mt-1 p-2.5 border rounded-xl text-xs text-slate-800 focus:ring-2 focus:ring-blue-500"
                />
              </div>

              {/* Media attachment */}
              <div>
                <label className="text-xs font-bold text-slate-700">រូបភាព ឬ វីដេអូភ្ជាប់ (Photo/Video)</label>
                <div className="mt-1 flex items-center gap-2">
                  <input
                    type="text"
                    value={mediaAttachment || ''}
                    onChange={(e) => setMediaAttachment(e.target.value)}
                    placeholder="ជ្រើស file ឬ link រូបភាព..."
                    className="flex-1 p-2 border rounded-xl text-xs"
                  />
                  <button
                    onClick={() => setMediaAttachment('kira_welcome_promo.png')}
                    className="px-3 py-2 bg-slate-100 hover:bg-slate-200 text-xs font-semibold rounded-xl text-slate-700"
                  >
                    ជ្រើស
                  </button>
                </div>
              </div>

              {/* Links box */}
              <div>
                <label className="text-xs font-bold text-slate-700">តំណភ្ជាប់បន្ថែម (Links / One per line)</label>
                <textarea
                  value={linksText}
                  onChange={(e) => setLinksText(e.target.value)}
                  rows={2}
                  className="w-full mt-1 p-2.5 border rounded-xl text-xs font-mono text-slate-800"
                />
              </div>

              {/* Live Preview */}
              <div className="bg-slate-50 border border-slate-200 rounded-xl p-3">
                <div className="text-[11px] font-bold text-blue-700 uppercase tracking-wider mb-1">
                  ទិដ្ឋភាពសារគំរូ (Live Preview):
                </div>
                <div className="text-xs bg-white p-2.5 rounded-lg border border-slate-200 space-y-1">
                  {mentionUsername && <div className="text-blue-600 font-bold">@new_member</div>}
                  <div className="text-slate-800">{messageText}</div>
                  <div className="text-blue-600 underline font-mono text-[11px] whitespace-pre-line">
                    {linksText}
                  </div>
                </div>
              </div>

              <button
                onClick={() => alert('បានរក្សាទុកសារគំរូដោយជោគជ័យ! រាល់ការចាប់ផ្តើមផុស នឹងប្រើប្រាស់សារថ្មីនេះ។')}
                className="w-full py-3 bg-blue-600 text-white font-bold rounded-xl text-xs hover:bg-blue-700 transition"
              >
                រក្សាទុកសារ (Save Template)
              </button>
            </div>
          )}

          {/* TAB 5: REPORTS */}
          {activeTab === 'reports' && (
            <div className="space-y-3">
              <div className="flex items-center justify-between border-b pb-2">
                <div>
                  <h3 className="font-bold text-base text-slate-900">របាយការណ៍នៃការផុសសារ (Live Logs)</h3>
                  <p className="text-xs text-slate-500">កំណត់ត្រា: {reports.length}</p>
                </div>
                <button
                  onClick={() => setReports([])}
                  className="text-xs text-red-600 hover:underline font-bold"
                >
                  លុបប្រវត្តិ
                </button>
              </div>

              <div className="space-y-2">
                {reports.length === 0 ? (
                  <div className="text-center py-12 text-slate-400 text-xs">មិនទាន់មានប្រវត្តិនៃការផុសនៅឡើយទេ</div>
                ) : (
                  reports.map((r) => (
                    <div key={r.id} className="bg-white border rounded-xl p-3 text-xs space-y-1">
                      <div className="flex items-center justify-between font-bold">
                        <span className="text-slate-800">{r.group}</span>
                        <span
                          className={`px-2 py-0.5 rounded text-[10px] ${
                            r.status === 'SUCCESS'
                              ? 'bg-green-100 text-green-700'
                              : r.status === 'FLOOD_WAIT'
                              ? 'bg-blue-100 text-blue-700'
                              : 'bg-red-100 text-red-700'
                          }`}
                        >
                          {r.status === 'SUCCESS'
                            ? 'ជោគជ័យ'
                            : r.status === 'FLOOD_WAIT'
                            ? `Flood Wait (${r.floodSeconds}s)`
                            : 'បរាជ័យ'}
                        </span>
                      </div>
                      <div className="text-slate-600">
                        សមាជិក: <span className="font-semibold">{r.member}</span> (@{r.username})
                      </div>
                      <div className="text-slate-500 italic text-[11px] line-clamp-1">{r.message}</div>
                      <div className="text-[10px] text-slate-400">{r.time}</div>
                    </div>
                  ))
                )}
              </div>
            </div>
          )}

          {/* TAB 6: SETTINGS */}
          {activeTab === 'settings' && (
            <div className="space-y-4">
              <div className="border-b pb-2">
                <h3 className="font-bold text-base text-slate-900">ការកំណត់ប្រព័ន្ធ (Settings)</h3>
                <p className="text-xs text-slate-500">ការការពារគណនី និង Foreground Service</p>
              </div>

              {/* Safe delay slider */}
              <div className="bg-blue-50 border border-blue-200 rounded-2xl p-4 space-y-2">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-slate-800">Safe Sending Interval:</span>
                  <span className="text-sm font-extrabold text-blue-700">{safeDelay} វិនាទី</span>
                </div>
                <input
                  type="range"
                  min="20"
                  max="120"
                  step="5"
                  value={safeDelay}
                  onChange={(e) => setSafeDelay(Number(e.target.value))}
                  className="w-full accent-blue-600 cursor-pointer"
                />
                <p className="text-[11px] text-slate-500">
                  គម្លាតពេលចន្លោះសារនីមួយៗ រួមបញ្ចូលទាំង Random Jitter 3-7 វិនាទី ដើម្បីការពារការបិទគណនីពី Telegram
                </p>
              </div>

              <div className="bg-white border rounded-2xl p-4 space-y-2">
                <h4 className="text-xs font-bold text-slate-800">Foreground Service & Battery</h4>
                <p className="text-[11px] text-slate-500">
                  អនុញ្ញាតឱ្យកម្មវិធីដំណើរការ Background មិនគាំងពេលបិទអេក្រង់ទូរស័ព្ទដៃ
                </p>
                <div className="flex gap-2">
                  <button
                    onClick={() => alert('កំពុងស្នើសុំបិទ Battery Optimization...')}
                    className="flex-1 py-2 bg-slate-100 hover:bg-slate-200 rounded-lg text-xs font-semibold text-slate-700"
                  >
                    បិទ Battery Optimization
                  </button>
                  <button
                    onClick={() => alert('សិទ្ធិ Notification បានបើក!')}
                    className="flex-1 py-2 bg-slate-100 hover:bg-slate-200 rounded-lg text-xs font-semibold text-slate-700"
                  >
                    សិទ្ធិ Notification
                  </button>
                </div>
              </div>

              <div className="text-center p-3 text-xs text-slate-400 space-y-1">
                <p className="font-bold text-slate-700">KIRA Auto Post v1.0.0</p>
                <p>បង្កើតឡើងដោយ KIRA Gaming</p>
                <p className="text-[10px]">Telegram User Account (TDLib 1.8.56 Native Engine)</p>
              </div>
            </div>
          )}
        </div>

        {/* Bottom Navigation matching Image 2 */}
        <div className="bg-white border-t border-slate-200 px-2 py-1.5 flex items-center justify-around text-slate-600">
          <button
            onClick={() => setActiveTab('home')}
            className={`flex flex-col items-center py-1 px-2 rounded-xl transition ${
              activeTab === 'home' ? 'text-blue-600 font-bold' : 'text-slate-600'
            }`}
          >
            {activeTab === 'home' ? (
              <div className="bg-blue-100 text-blue-600 px-3 py-1 rounded-full mb-0.5">
                <Home className="w-5 h-5 fill-current" />
              </div>
            ) : (
              <Home className="w-5 h-5 mb-0.5" />
            )}
            <span className="text-[10px]">ទំព័រដើម</span>
          </button>

          <button
            onClick={() => setActiveTab('accounts')}
            className={`flex flex-col items-center py-1 px-2 rounded-xl transition ${
              activeTab === 'accounts' ? 'text-blue-600 font-bold' : 'text-slate-600'
            }`}
          >
            <User className="w-5 h-5 mb-0.5" />
            <span className="text-[10px]">គណនី</span>
          </button>

          <button
            onClick={() => setActiveTab('groups')}
            className={`flex flex-col items-center py-1 px-2 rounded-xl transition ${
              activeTab === 'groups' ? 'text-blue-600 font-bold' : 'text-slate-600'
            }`}
          >
            <Users className="w-5 h-5 mb-0.5" />
            <span className="text-[10px]">ក្រុម</span>
          </button>

          <button
            onClick={() => setActiveTab('post')}
            className={`flex flex-col items-center py-1 px-2 rounded-xl transition ${
              activeTab === 'post' ? 'text-blue-600 font-bold' : 'text-slate-600'
            }`}
          >
            <Send className="w-5 h-5 mb-0.5" />
            <span className="text-[10px]">ផុស</span>
          </button>

          <button
            onClick={() => setActiveTab('reports')}
            className={`flex flex-col items-center py-1 px-2 rounded-xl transition ${
              activeTab === 'reports' ? 'text-blue-600 font-bold' : 'text-slate-600'
            }`}
          >
            <BarChart3 className="w-5 h-5 mb-0.5" />
            <span className="text-[10px]">របាយការណ៍</span>
          </button>

          <button
            onClick={() => setActiveTab('settings')}
            className={`flex flex-col items-center py-1 px-2 rounded-xl transition ${
              activeTab === 'settings' ? 'text-blue-600 font-bold' : 'text-slate-600'
            }`}
          >
            <SettingsIcon className="w-5 h-5 mb-0.5" />
            <span className="text-[10px]">ការកំណត់</span>
          </button>
        </div>
      </div>

      {/* Code & GitHub Workflow Modal */}
      {showCodeModal && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-xs z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-lg w-full max-h-[85vh] flex flex-col shadow-2xl overflow-hidden">
            <div className="px-5 py-4 bg-slate-900 text-white flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Code2 className="w-5 h-5 text-blue-400" />
                <h3 className="font-bold text-sm">គម្រោង Android (Kotlin + TDLib + GitHub Actions)</h3>
              </div>
              <button
                onClick={() => setShowCodeModal(false)}
                className="text-slate-400 hover:text-white text-lg font-bold"
              >
                ✕
              </button>
            </div>
            <div className="p-5 overflow-y-auto space-y-4 text-xs text-slate-700">
              <div className="bg-emerald-50 border border-emerald-200 text-emerald-800 p-3 rounded-xl">
                <p className="font-bold">✔ រាល់ File គម្រោង Android ទាំងអស់ត្រូវបានបង្កើតរួចរាល់ 100%៖</p>
                <ul className="list-disc list-inside mt-1 space-y-0.5 font-mono text-[11px]">
                  <li>settings.gradle.kts</li>
                  <li>build.gradle.kts</li>
                  <li>app/build.gradle.kts</li>
                  <li>gradle/libs.versions.toml & gradle-wrapper.properties</li>
                  <li>app/src/main/AndroidManifest.xml</li>
                  <li>app/src/main/java/com/kira/autopost/... (Kotlin Sources)</li>
                  <li>app/src/main/res/values/strings.xml (Khmer + English)</li>
                  <li>app/src/main/res/mipmap-* (Icons ពី Image 1 ផ្ទៃខ្មៅ)</li>
                  <li>.github/workflows/build.yml</li>
                  <li>README.md (របៀប Push ទៅ GitHub & Download APK)</li>
                </ul>
              </div>

              <div>
                <h4 className="font-bold text-slate-900 mb-1">ពាក្យបញ្ជាដើម្បី Push ទៅ GitHub៖</h4>
                <pre className="bg-slate-900 text-green-400 p-3 rounded-xl font-mono text-[11px] overflow-x-auto">
{`git init
git add .
git commit -m "KIRA Auto Post full Android project"
git branch -M main
git remote add origin https://github.com/YOUR_USER/kira-auto-post.git
git push -u origin main`}
                </pre>
              </div>

              <div className="bg-blue-50 border border-blue-200 p-3 rounded-xl">
                <p className="font-bold text-blue-900 mb-1">របៀបទាញយក APK ពី GitHub Actions:</p>
                <p>
                  ពេល Push រួច សូមចូលទៅកាន់ GitHub Repo របស់អ្នក ចុចលើ Tab <strong>Actions</strong> អ្នកនឹងឃើញ
                  Workflow <strong>Build KIRA Auto Post APK</strong> កំពុងដំណើរការដោយស្វ័យប្រវត្តិតាមរយៈ Gradle 8.7 & JDK 17
                  ហើយនឹង Upload File <strong>app-debug-apk</strong> សម្រាប់ Download បានភ្លាមៗ!
                </p>
              </div>
            </div>
            <div className="p-4 bg-slate-50 border-t flex justify-end">
              <button
                onClick={() => setShowCodeModal(false)}
                className="px-4 py-2 bg-blue-600 text-white font-bold rounded-xl text-xs hover:bg-blue-700"
              >
                យល់ព្រម (Close)
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
