function TaskItem({ task, onDelete, onUpdate }) {
  return (
    <li>
      {task.title} - {task.status}

      <button onClick={() => onDelete(task.id)}>
        ❌
      </button>

      <button onClick={() => onUpdate(task.id, "IN_PROGRESS")}>
        ▶
      </button>

      <button onClick={() => onUpdate(task.id, "DONE")}>
        ✅
      </button>
    </li>
  );
}

export default TaskItem;