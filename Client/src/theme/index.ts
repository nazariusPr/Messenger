export const theme = {
  colors: {
    primary: "#2563eb", // Blue-600 — vibrant primary action
    secondary: "#f59e0b", // Amber-500 — warm secondary accent
    background: "#ffffff", // Pure white for clean base
    surface: "#f9fafb", // Subtle background layers
    text: "#111827", // Gray-900 — strong readable text
    muted: "#6b7280", // Gray-500 — for descriptions, timestamps
    border: "#e5e7eb", // Gray-200 — for borders/separators
    error: "#dc2626", // Red-600 — for errors
    success: "#16a34a", // Green-600 — for success messages
    warning: "#facc15", // Yellow-400 — for warning messages
    info: "#0ea5e9", // Sky-500 — for info banners
  },

  fontSizes: {
    small: "12px",
    medium: "16px",
    large: "24px",
  },

  spacing: {
    xs: "4px",
    sm: "8px",
    md: "16px",
    lg: "32px",
  },

  borderRadius: {
    sm: "4px",
    md: "8px",
    lg: "16px",
  },

  breakpoints: {
    mobile: "480px",
    tablet: "768px",
    desktop: "1024px",
  },

  zIndex: {
    hide: -1,
    auto: "auto",
    base: 0,
    dropdown: 10,
    sticky: 100,
    fixed: 500,
    modalBackdrop: 900,
    modal: 1000,
    tooltip: 1100,
  },
};
