import { useState, useEffect, useCallback } from "react";
import { DragDropContext, Droppable, Draggable } from "@hello-pangea/dnd";
import { motion } from "framer-motion";
import { AnimatePresence } from "framer-motion";
import TaskList from "./TaskList";
import './App.css';

function App() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [token, setToken] = useState(localStorage.getItem("token") || "");
  const [tasks, setTasks] = useState([]);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [filter, setFilter] = useState("ALL");
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(50);
  const [totalPages, setTotalPages] = useState(0);
  const [search, setSearch] = useState("");
  const [status,setStatus] = useState("TODO");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [onlyMine, setOnlyMine] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [editTitle, setEditTitle] = useState("");
  const [editDescription, setEditDescription] = useState("");

  // ✅ LOGIN (JWT = TEXT)  test change
  const handleLogin = async () => {
    console.log("LOGIN DATA:", username, password);

    try {
    const res = await fetch(`${process.env.REACT_APP_API_URL}/auth/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ username, password }),
      });

      if (!res.ok) {
        const err = await res.text();
        setError("Login failed");
        return;
      }

      const token = await res.text(); // ✅ مهم

      localStorage.setItem("token", token);
      setToken(token);

      console.log("TOKEN:", token);
    } catch (err) {
     setError("Login failed");
    }
  };

  // ✅ FETCH TASKS (با useCallback برای حل warning)
  const fetchTasks = useCallback(async () => {
  setLoading(true);
    const token = localStorage.getItem("token");
    console.log("TOKEN:", token);
  //  localStorage.removeItem("token");
    if (!token) {
      setLoading(false);
      return;
    }

    try {
    let url;
     if (onlyMine) {
          url = `${process.env.REACT_APP_API_URL}/tasks?mine=true&page=${page}&size=${size}&title=${search}`;
        } else {
          url = `${process.env.REACT_APP_API_UR}/tasks?page=${page}&size=${size}&title=${search}`;
        }
      const res = await fetch(
        url,{
          headers: {
            Authorization: "Bearer " + token,
          },
        }
      );

      if (!res.ok) {
        setError("Fetch failed");
        return;
      }

      const data = await res.json();

      setTasks(data.data || []);
      setTotalPages(data.totalPages || 0);
      setLoading(false);
    } catch (err) {
      setError("Error fetching tasks");
    }
  }, [page, search, onlyMine,size]);

  // ✅ CREATE TASK
  const handleCreate = async () => {
    const token = localStorage.getItem("token");

    try {
      const res = await fetch(`${process.env.REACT_APP_API_URL}/tasks`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + token,
        },
          body: JSON.stringify({
          title,
          description,
          status,
        }),
      });

if (!res.ok) {
  const err = await res.text();
  console.log(err);
  setError("Create failed");
  return;
}
       if (res.ok) {
          const newTask = await res.json();
          console.log("CREATE:", { title, description, status });
          // 👇 مهم
          //setTasks(prev => [newTask, ...prev]);
          fetchTasks();
          setTitle("");
          setDescription("");
        }
    } catch (err) {
      setError("Error creating task");
    }
  };

  // ✅ DELETE
  const deleteTask = async (id) => {
    const token = localStorage.getItem("token");

    try {
      await fetch(`${process.env.REACT_APP_API_URL}/tasks/${id}`, {
        method: "DELETE",
        headers: {
          Authorization: "Bearer " + token,
        },
      });

      fetchTasks();
    } catch (err) {
      setError("Delete error");
    }
  };

  // ✅ UPDATE STATUS
  const updateStatus = async (id, newStatus) => {
    const token = localStorage.getItem("token");

    try {
      await fetch(
        `${process.env.REACT_APP_API_URL}/tasks/${id}/status`,
        {
          method: "PATCH",
          headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + token,
          },
          body: JSON.stringify({
            status: newStatus,
          }),
        }
      );

      fetchTasks();
    } catch (err) {
      setError("Update error");
    }
  };

  const updateTask = async (id) => {
    const token = localStorage.getItem("token");

    try {
      const res = await fetch(
        `${process.env.REACT_APP_API_URL}/tasks/${id}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + token,
          },
          body: JSON.stringify({
            title: editTitle,
            description: editDescription,
          }),
        }
      );

      if (!res.ok) {
        setError("Update failed");
        return;
      }

      fetchTasks();

      setEditingId(null);
      setEditTitle("");
      setEditDescription("");

    } catch (err) {
      setError("Update error");
    }
  };

  // ✅ FIX useEffect warning
  useEffect(() => {
    if (token) {
      fetchTasks();
    }
  }, [token, page, search, fetchTasks]);

  const logout = () => {
    localStorage.removeItem("token");
    setToken("");
    setTasks([]);
  };

  const filteredTasks =
    filter === "ALL"
      ? tasks
      : tasks.filter((task) => task.status === filter);

   const handleDragEnd = async (result) => {
     if (!result.destination) return;

     const taskId = result.draggableId;
     const newStatus = result.destination.droppableId;

     await updateStatus(taskId, newStatus);
   };
if (!token) {
  return (
    <div className="login-container">
      <div className="login-box">
      <h2>Login</h2>

      <input
        placeholder="username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />

      <input
        type="password"
        placeholder="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />

      <button onClick={handleLogin}>Login</button>
    </div>
    </div>
  );
}
  return (
    <div className="container">

        <div className = "sidebar">
        <h2 className="sidebar-title">
          Task Manager
        </h2>

        <button
          className={onlyMine ? "active" : ""}
          onClick={() => setOnlyMine(!onlyMine)}
        >
          {onlyMine ? "Showing Mine" : "All Tasks"}
        </button>
        <button onClick={logout}>
          Logout
        </button>
        </div>

            <div className = "main"><h2>Kanban Board</h2>

            {loading && <p>Loading...</p>}
            {error && <p className="error">{error}</p>}


            {tasks.length === 0 && <p>No tasks yet 🚀</p>}
           <DragDropContext onDragEnd={handleDragEnd}>
             <div className="board">

               {["TODO", "IN_PROGRESS", "DONE"].map(status => (
                 <Droppable droppableId={status} key={status}>
                   {(provided) => (
                     <motion.div layout className="column"
                       ref={provided.innerRef}
                       {...provided.droppableProps}
                     >
                       <h3>{status}</h3>
                    <AnimatePresence>
                       {tasks
                         .filter(t => t.status === status)
                         .map((task, index) => (
                           <Draggable
                             key={task.id}
                             draggableId={task.id.toString()}
                             index={index}
                           >
                             {(provided) => (
                               <motion.div
                                  className="task"
                                  layout
                                  initial={{ opacity: 0, y: 20 }}
                                  animate={{ opacity: 1, y: 0 }}
                                  exit={{ opacity: 0, scale: 0.8, y: -10 }}
                                  whileDrag={{ scale: 1.05 }}
                                  whileHover={{ scale: 1.03 }}
                                 ref={provided.innerRef}
                                 {...provided.draggableProps}
                                 {...provided.dragHandleProps}
                               >
                                 {editingId === task.id ? (
                                   <>
                                     <input
                                       value={editTitle}
                                       onChange={(e) => setEditTitle(e.target.value)}
                                     />

                                     <input
                                       value={editDescription}
                                       onChange={(e) => setEditDescription(e.target.value)}
                                     />

                                     <button onClick={() => updateTask(task.id)}>
                                       Save
                                     </button>

                                     <button onClick={() => setEditingId(null)}>
                                       Cancel
                                     </button>
                                   </>
                                 ) : (
                                   <>
                                     <h4>{task.title}</h4>
                                     <p>{task.description}</p>
                                     {task.createdAt && (
                                       <p className="date">
                                         {new Date(task.createdAt).toLocaleString()}
                                       </p>
                                     )}
                                     <button
                                       onClick={() => {
                                         setEditingId(task.id);
                                         setEditTitle(task.title);
                                         setEditDescription(task.description);
                                       }}
                                     >
                                       Edit
                                     </button>
                                   </>
                                 )}

                                 <button onClick={() => deleteTask(task.id)}>
                                       Delete
                                 </button>


                               </motion.div>
                             )}
                           </Draggable>
                         ))}
                        </AnimatePresence>
                       {provided.placeholder}
                     </motion.div>
                   )}
                 </Droppable>
               ))}

             </div>
           </DragDropContext>

<div className = "create-task"><h3> Create a new task</h3>
            <input
             placeholder="title"
             value = {title}
             onChange={(e) => setTitle(e.target.value)}
             />

             <input
             placeholder ="description"
             value ={description}
             onChange={(e) => setDescription(e.target.value)}
             />

             <div className="status-picker">

               <button
                 className={status === "TODO" ? "active todo" : "todo"}
                 onClick={() => setStatus("TODO")}
               >
                 TODO
               </button>

               <button
                 className={status === "IN_PROGRESS"
                   ? "active progress"
                   : "progress"}
                 onClick={() => setStatus("IN_PROGRESS")}
               >
                 IN_PROGRESS
               </button>

               <button
                 className={status === "DONE" ? "active done" : "done"}
                 onClick={() => setStatus("DONE")}
               >
                 DONE
               </button>
             </div>
            <div className= "create-btn">
             <button  onClick = {handleCreate}>Create</button>
            </div>
            </div>

        </div>




    </div>
  );
}

export default App;