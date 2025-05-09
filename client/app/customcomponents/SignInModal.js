'use client';
import React, { useState } from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Modal from '@mui/material/Modal';
import GoogleSignIn from './GoogleSignIn';

const style = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: 400,
  bgcolor: 'rgba(31, 41, 55, 0.95)',
  backdropFilter: 'blur(8px)',
  boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
  p: 4,
  borderRadius: '1rem',
  border: '1px solid rgba(75, 85, 99, 0.4)',
};

export default function SignInModal() {
  const [open, setOpen] = useState(false);
  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  return (
    <div>
      <div id='button-wrapper' className='relative group'>
        <div className='absolute -inset-0.5 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-lg blur opacity-30 group-hover:opacity-100 transition duration-1000 group-hover:duration-200'></div>
        <Button 
          className='outlined relative px-6 py-2 bg-gray-800 text-white rounded-lg border border-gray-700 font-medium text-base'
          onClick={handleOpen}
        >
          Sign In
        </Button>
      </div>
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <Box sx={style}>
          <h2 className='text-3xl font-bold text-center mb-6 bg-gradient-to-r from-indigo-400 to-purple-400 bg-clip-text text-transparent'>
            Welcome Back
          </h2>
          <GoogleSignIn />
        </Box>
      </Modal>
    </div>
  );
}