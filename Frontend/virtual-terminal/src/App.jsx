import { useState, useRef, useEffect } from 'react'
import './App.css'

function App() {
  const [history, setHistory] = useState([
    { type: 'output', text: 'Welcome to Virtual Diego_Linux v1.0. Type a command to start. If you not have knowledge about commands, please type: "manual". ' }
  ]);
  const [input, setInput] = useState('');
  const bottomRef = useRef(null);
  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [history]);

  const handleKeyDown = async (e) => {
    if (e.key === 'Enter') {
      if (!input.trim()) return;

      const commandToRun = input.trim();
      setInput(''); 
      setHistory(prev => [...prev, { type: 'input', text: `diego@virtual-os:~$ ${commandToRun}` }]);
      if (commandToRun === 'clear') {
        setHistory([]);
        return;
      }
      try {
        const response = await fetch('https://virtualos-backend.onrender.com/execute', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ command: commandToRun })
        });

        const text = await response.text();
        if (text) {
          setHistory(prev => [...prev, { type: 'output', text: text }]);
        }
      } catch (error) {
        setHistory(prev => [...prev, { type: 'error', text: 'Error: Connection to backend refused.' }]);
      }
    }
  };

  return (
    <div className="terminal" onClick={() => document.getElementById('cmd-input').focus()}>
      <div className="history">
        {history.map((line, i) => (
          <div key={i} className={`line ${line.type}`}>
            {}
            <pre>{line.text}</pre>
          </div>
        ))}
        <div ref={bottomRef} /> {}
      </div>
      
      <div className="input-line">
        <span className="prompt">diego@virtual-os:~$</span>
        <input
          id="cmd-input"
          type="text"
          value={input}
          onChange={e => setInput(e.target.value)}
          onKeyDown={handleKeyDown}
          autoFocus
          autoComplete="off"
          spellCheck="false"
        />
      </div>
    </div>
  )
}

export default App;